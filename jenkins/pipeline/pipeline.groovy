pipeline {

    environment {
        BASE_GIT_URL = 'https://github.com/caferrerbeam'
        APP_REPO_URL = "${env.BASE_GIT_URL}/${nombre_ms}.git"
        INFRA_REPO_URL = "${env.BASE_GIT_URL}/seminariodocker.git"
        DOCKER_IMAGE = "eamquindio/${nombre_ms}"
        DEPLOY_FOLDER = "deploy/k8/definiciones/${nombre_ms}"
    }

    agent any
    stages {
        stage("Checkout app-code") {
            steps {
            //se esta crando una carpeta....
               dir('app') {
                    git url:"${env.APP_REPO_URL}" , branch: "${version}"
                } 
            }
        }
         
         stage("Checkout deploy-code") {
            steps {
               dir('deploy') {
                    git url:"${env.INFRA_REPO_URL}" , branch: "jenkins"
                } 
            }
        }
        
         stage("Build image") {
            steps {
                dir('app') {
                    script {
                        dockerImage = docker.build("${env.DOCKER_IMAGE}:${version}")
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
                sh "sed -i 's:DOCKER_IMAGE:${env.DOCKER_IMAGE}:g' ${DEPLOY_FOLDER}/deployment.yaml"
                sh "sed -i 's:TAG:${version}:g' ${DEPLOY_FOLDER}/deployment.yaml"
                
                step([$class: 'KubernetesEngineBuilder', 
                        projectId: "nice-root-288300",
                        clusterName: "cluster-camilo",
                        zone: "us-west1-a",
                        manifestPattern: "${DEPLOY_FOLDER}/",
                        credentialsId: "seminario",
                        verifyDeployments: true])
            }
        }
    }
}