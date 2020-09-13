pipeline {
    agent any
    stages {
        stage("Checkout app-code") {
            steps {
               dir('app') {
                    git url:"https://github.com/eamquindio/practicas_back_estudiante.git" , branch: "develop"
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
                        dockerImage = docker.build("eamquindio/estudiante-ms:1.0")
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
                step([$class: 'KubernetesEngineBuilder', 
                        projectId: "nice-root-288300",
                        clusterName: "cluster-camilo",
                        zone: "us-west1-a",
                        manifestPattern: 'deploy/k8/estudiantes/',
                        credentialsId: "seminario",
                        verifyDeployments: true])
            }
        }

    }
}