import org.jetbrains.annotations.NotNull;
import software.amazon.awscdk.App;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.CertificateValidation;
import software.amazon.awscdk.services.ec2.Peer;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.msk.alpha.ClusterConfigurationInfo;
import software.amazon.awscdk.services.msk.alpha.KafkaVersion;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneAttributes;
import software.amazon.awscdk.services.secretsmanager.Secret;

import java.util.List;
import java.util.Map;

public class RoteStack extends Stack {
    public RoteStack(final App scope, final String id) {
        this(scope, id, null);
    }

    public RoteStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        var domainName = "rote.apps.ryanossandon.com";
        var version = "0994e54";
        var profile = "prod";
        var googleOAuthClientId = "162744778869-jkl6qurhtus9mg28gtgpr9mge73qvo8t.apps.googleusercontent.com";

        Vpc vpc = Vpc.Builder.create(this, "Vpc")
                .maxAzs(3)
                .build();

        Cluster cluster = Cluster.Builder.create(this, "EcsCluster")
                .vpc(vpc).build();

        var repository = Repository.fromRepositoryArn(this, "ECR", ManualResources.EcrRepoArn);

        var kafkaCluster = kafkaCluster(vpc);

        var hostedZone = HostedZone.fromHostedZoneAttributes(this, "HostedZone", ManualResources.Route53HostedZone);

        var certificate = Certificate.Builder.create(this, "WebServiceCertificate")
                .certificateName("WebServiceCertificate")
                .domainName(domainName)
                .validation(CertificateValidation.fromDns(hostedZone))
                .build();

        var googleOAuthSecret = Secret.fromSecretCompleteArn(this, "GoogleAuthSecret", ManualResources.GoogleOAuthSecretArn);
        var clientSecretProp = "spring.security.oauth2.client.registration.google.client-secret";
        var clientIdProp = "spring.security.oauth2.client.registration.google.client-id";
        var secrets = Map.of(clientSecretProp, software.amazon.awscdk.services.ecs.Secret.fromSecretsManager(googleOAuthSecret));

        var webService = ApplicationLoadBalancedFargateService.Builder.create(this, "WebService")
                .cluster(cluster)
                .cpu(512)
                .desiredCount(2)
                .domainZone(hostedZone)
                .domainName(domainName)
                .certificate(certificate)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromEcrRepository(repository, "rote-webService-" + version))
                                .environment(Map.of(
                                        "SPRING_PROFILES_ACTIVE", profile,
                                        "kafka.targetHost", kafkaCluster.getBootstrapBrokersTls(),
                                        "kafka.environmentName", profile,
                                        "kafka.tls", "true",
                                        "server.use-forward-headers", "true",
                                        "server.forward-headers-strategy", "NATIVE",
                                        clientIdProp, googleOAuthClientId
                                ))
                                .secrets(secrets)
                                .build())
                .memoryLimitMiB(2048)
                .publicLoadBalancer(true)
                .build();

        webService.getTargetGroup().configureHealthCheck(HealthCheck.builder().path("/system/ping").build());

        var tradingEngineServiceTaskDef = FargateTaskDefinition.Builder.create(this, "TradingEngineServiceTaskDef")
                .cpu(1024)
                .memoryLimitMiB(4096)
                .build();

        var tradingEngineContainerDef = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromEcrRepository(repository, "rote-trading-" + version))
                .logging(LogDrivers.awsLogs(AwsLogDriverProps.builder().streamPrefix("TradingEngine").build()))
                .environment(Map.of(
                        "SPRING_PROFILES_ACTIVE", profile,
                        "kafka.targetHost", kafkaCluster.getBootstrapBrokersTls(),
                        "kafka.environmentName", profile,
                        "kafka.tls", "true",
                        "tradingEngineContext.provider", "fresh"
                ))
                .build();

        tradingEngineServiceTaskDef.addContainer("main", tradingEngineContainerDef);

        FargateService.Builder.create(this, "TradingEngineService")
                .desiredCount(1)
                .cluster(cluster)
                .taskDefinition(tradingEngineServiceTaskDef)
                .build();
    }

    private software.amazon.awscdk.services.msk.alpha.Cluster kafkaCluster(Vpc vpc) {
        var kafkaSecurityGroup = SecurityGroup.Builder.create(this, "KafkaSecurityGroup")
                .vpc(vpc)
                .build();

        kafkaSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(9094));

        return software.amazon.awscdk.services.msk.alpha.Cluster.Builder.create(this, "Kafka")
                .vpc(vpc)
                .removalPolicy(RemovalPolicy.DESTROY)
                .clusterName("rote")
                .securityGroups(List.of(kafkaSecurityGroup))
                .kafkaVersion(KafkaVersion.V2_6_0)
                .build();
    }
}