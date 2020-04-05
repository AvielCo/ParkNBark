pipeline {
	agent any
		stages {
			stage("Checkout") {
				steps {
					git url: 'https://github.com/AvielCo/ParkNBark'
				}
			}
			stage("Compile") {
				steps {
					sh "./gradlew compileJava"
				}
			}
		}
	}
