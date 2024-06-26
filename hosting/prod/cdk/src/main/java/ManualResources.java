import software.amazon.awscdk.services.route53.HostedZoneAttributes;

public class ManualResources {
    public static String GoogleOAuthSecretArn = "arn:aws:secretsmanager:us-east-2:048545230017:secret:GoogleAuth0Secret-Yc5ijL";
    public static String DatadogApiKeySecretArn = "arn:aws:secretsmanager:us-east-2:048545230017:secret:DatadogApiKey-o39Chf";
    public static HostedZoneAttributes Route53HostedZone = HostedZoneAttributes.builder().hostedZoneId("Z08867352EZ6TPDCMZOAX").zoneName("apps.ryanossandon.com").build();
}
