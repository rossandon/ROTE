import software.amazon.awscdk.App;
import software.amazon.awscdk.SecretsManagerSecretOptions;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.ecs.patterns.QueueProcessingFargateService;
import software.amazon.awscdk.services.elasticache.CfnSecurityGroupIngress;
import software.amazon.awscdk.services.msk.alpha.KafkaVersion;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.secretsmanager.Secret;

import java.util.List;
import java.util.Map;

public class RoteStack extends Stack {
    public RoteStack(final App scope, final String id) {
        this(scope, id, null);
    }

    public RoteStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        Vpc vpc = Vpc.Builder.create(this, "Vpc")
                .maxAzs(3)
                .build();

        Cluster cluster = Cluster.Builder.create(this, "EcsCluster")
                .vpc(vpc).build();

        Repository repository = Repository.Builder.create(this, "ECR")
                .repositoryName("rote")
                .build();

        var kafkaCluster = software.amazon.awscdk.services.msk.alpha.Cluster.Builder.create(this, "Kafka")
                .vpc(vpc)
                .clusterName("rote")
                .kafkaVersion(KafkaVersion.V2_6_0)
                .build();

        var googleOAuthSecret = Secret.fromSecretCompleteArn(this, "GoogleAuthSecret", "arn:aws:secretsmanager:us-east-2:048545230017:secret:GoogleAuth0Secret-Yc5ijL");

        var clientSecretProp = "spring.security.oauth2.client.registration.google.client-secret";
        var clientIdProp = "spring.security.oauth2.client.registration.google.client-id";

        var secrets = Map.of(clientSecretProp, software.amazon.awscdk.services.ecs.Secret.fromSecretsManager(googleOAuthSecret));

        ApplicationLoadBalancedFargateService.Builder.create(this, "WebService")
                .cluster(cluster)
                .cpu(512)
                .desiredCount(2)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromEcrRepository(repository, "rote-webService-023f64c"))
                                .environment(Map.of(
                                        "SPRING_PROFILES_ACTIVE", "prod",
                                        "kafka.targetHost", kafkaCluster.getBootstrapBrokersTls(),
                                        "kafka.environmentName", "prod",
                                        clientIdProp, "162744778869-jkl6qurhtus9mg28gtgpr9mge73qvo8t.apps.googleusercontent.com"
                                ))
                                .secrets(secrets)
                                .build())
                .memoryLimitMiB(2048)
                .publicLoadBalancer(true)
                .build();

        QueueProcessingFargateService.Builder.create(this, "TradingEngineService")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(2048)
                .image(ContainerImage.fromEcrRepository(repository, "rote-webService-023f64c"))
                .environment(Map.of(
                        "SPRING_PROFILES_ACTIVE", "prod",
                        "kafka.targetHost", kafkaCluster.getBootstrapBrokersTls(),
                        "kafka.environmentName", "prod",
                        "tradingEngineContext.provider", "fresh"
                ))
                .build();
    }
}