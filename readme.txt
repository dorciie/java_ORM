setenv.cmd
mvn clean package dependency:copy-dependencies
mvn -DskipTests package

