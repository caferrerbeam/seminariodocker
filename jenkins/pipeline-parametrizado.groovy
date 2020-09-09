pipeline {
    environment {
     TAG="${version}_${env.BUILD_ID}"
     DOCKER_IMAGE="eamquindio/${project}:${env.TAG}"
     GIT_REPO="https://github.com/caferrerbeam/${project}.git"
     DEPLOY_FOLDER="deploy/k8/parametrized/${project}/"
     DEPLOY_FILE="${env.DEPLOY_FOLDER}/deployment.yaml"
   }

    agent any
    stages {
        stage("Checkout app-code") {
            steps {
               dir('app') {
                    git url:"${env.GIT_REPO}" , branch: "${version}"
                } 
            }
        }
         
         stage("Checkout deploy-code") {
            steps {
               dir('deploy') {
                    git url:"https://github.com/caferrerbeam/seminariodocker.git" , branch: "jenkins"
                } 
            }
        }
        
        stage("Build image") {
            steps {
                dir('app') {
                    script {
                        dockerImage = docker.build("${env.DOCKER_IMAGE}")
                    }
                }
            }
        }
        stage("Push image") {
            steps {
                script {
                    docker.withRegistry('', 'dockerhubclase') {
                    dockerImage.push()
                    }
                }
            }
        }

        stage('Deploy') {
            steps{

                sh "sed -i 's/DOCKER_IMAGE/${env.DOCKER_IMAGE}/g' ${env.DEPLOY_FILE}"

                step([$class: 'KubernetesEngineBuilder', 
                        projectId: "nice-root-288300",
                        clusterName: "cluster-camilo",
                        zone: "us-west1-a",
                        manifestPattern: "${env.DEPLOY_FOLDER}",
                        credentialsId: "seminario",
                        verifyDeployments: true])
            }
        }

    }
}