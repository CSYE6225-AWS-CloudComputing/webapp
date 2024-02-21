#add group
sudo groupadd csye6225

#add user to group
sudo useradd -g csye6225 -s /usr/sbin/nologin csye6225

sudo cp ./startup-scripts/webapp.service /etc/systemd/system

sudo chown -R csye6225:csye6225 /home/csye6225/webapp
sudo chmod 744 /home/csye6225/webapp

sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
