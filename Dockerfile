FROM oracle/glassfish:latest

MAINTAINER Víctor Fernández "vfrico@gmail.com"

# JSP pages requires JDK
RUN yum -y install java-1.8.0-openjdk-devel.x86_64  && \
    rm -rf /var/cache/yum

# Copy the compiled WAR to autodeploy folder
COPY target/predictor.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/

# Build with:
# docker build .
# Run with:
# docker run -p 8080:8080 -p 8181:8181 -p 4848:4848 -v /home/vfrico:/home/vfrico f2fe36e684d1