def CONFIG_FILE = 'service.yaml'
def JAR_FILE = 'lite-country-service-1.0-SNAPSHOT.jar'
def IMAGE_NAME = '$LITE_DOCKER_REGISTRY/country-service'

node('jdk8') {
    
    stage 'Clean workspace'
    
    deleteDir()
    
    stage 'Checkout files'
    
    checkout scm
    
    stage 'Gradle build'
    
    sh 'chmod 777 gradlew'
    sh './gradlew build'
    
    step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/**/*.xml'])
    
    stage 'Docker build'
    
    withEnv(["IMAGE_NAME=${IMAGE_NAME}"]) {
        sh '/usr/bin/docker build -t $IMAGE_NAME:$BUILD_NUMBER -t $IMAGE_NAME:latest .'
        sh '/usr/bin/docker push $IMAGE_NAME'
    }
}