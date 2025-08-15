pipeline {
    agent any
    environment {
        GITHUB_PAT = credentials('tracer-pat')
    }
    
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/arunkbabu90/PopMovies-and', branch: 'build/production', credentialsId: GITHUB_PAT
            }
        }

        stage('Copy google-services.json file to app') {
            steps {
                script {
                    withCredentials([
                        file(credentialsId: 'POPMOVIES-GOOGLE-SERVICES-JSON', variable: 'GOOGLE_SERVICES_JSON')
                    ]) {
                        bat 'copy "%GOOGLE_SERVICES_JSON%" app\\google-services.json'
                    }
                }
            }
        }

        stage('Copy keystore.properties file to project Root') {
            steps {
                script {
                    withCredentials([
                        file(credentialsId: 'POPMOVIES_KEYSTORE_PROPERTIES_FILE', variable: 'APP_KEYSTORE_PROPERTIES_FILE')
                    ]) {
                        bat 'copy "%APP_KEYSTORE_PROPERTIES_FILE%" keystore.properties'
                    }
                }
            }
        }

        stage('Copy Keystore file to app') {
            steps {
                script {
                    withCredentials([
                        file(credentialsId: 'POPMOVIES_KEYSTORE_FILE', variable: 'APP_KEYSTORE_FILE')
                    ]) {
                        bat 'copy "%APP_KEYSTORE_FILE%" app\\Pop Movies v1.jks'
                    }
                }
            }
        }

        stage('Build Release AAB') {
            steps {
               sh './gradlew clean bundleRelease'
            }
        }

        stage('Publish APP to Google Play Store to Production') {
            steps {
                script {
                    def recentChanges = readFile('release-notes.txt')

                    androidApkUpload googleCredentialsId: 'PLAY_CONSOLE_DEV_API_JSON_FILE',
                                     filesPattern: '**/app/build/outputs/bundle/release/*.aab',
                                     trackName: 'production',
                                     inAppUpdatePriority: '2',
                                     rolloutPercentage: '100',
                                     recentChangeList: [
                                         [language: 'en-US', text: recentChanges]
                                     ]
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: '**/app/build/outputs/bundle/release/*.aab', allowEmptyArchive: true

            // Copy the artifacts to the specific directory
            script {
                def destinationDir = 'E:\\Outputs\\Android_Artifacts\\PopMovies\\'
                def appBundlePath = 'app\\build\\outputs\\bundle\\release\\'
                bat "copy ${appBundlePath} ${destinationDir}\\app"
            }
        }
    }
}
