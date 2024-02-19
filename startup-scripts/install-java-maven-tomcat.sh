# Install unzip and openjdk
sudo yum install -y java-17-openjdk maven tomcat

#Java version change
echo 2 | sudo alternatives --config java

#start tomcat service
sudo systemctl enable tomcat
sudo systemctl start tomcat