#add group
sudo groupadd csye6225

#add user to group
sudo useradd -s /usr/sbin/nologin -g csye6225 csye6225

sudo unzip webapp.zip
ls /home/csye6225/
ls /home/csye6225/webapp
ls /home/csye6225/webapp/target
sudo cp ./startup-scripts/webapp.service /etc/systemd/system

sudo chown -R csye6225:csye6225 /home/csye6225/webapp
sudo chmod 744 /home/csye6225/webapp

sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
