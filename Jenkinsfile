@Library('jenkins-shared-library') _
pipelineTemplate(
    buildType: 'gradle',
    agentLabel: 'kube-gradle-agent',
    projectKey: 'sboot-order-processor',
    appImage: '361769563347.dkr.ecr.us-east-1.amazonaws.com/sboot-order-processor',
    awsBucket: 'thoggs-sboot-order-processor',
    releaseBranch: 'main',
    awsRegion: 'us-east-1',
    awsRegistry: '361769563347.dkr.ecr.us-east-1.amazonaws.com',
    awsBucketReleaseFileName: 'release-version.txt',
    awsBucketSnapshotFileName: 'snapshot-version.txt'
)