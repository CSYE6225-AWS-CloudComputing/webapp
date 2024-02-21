#add group
sudo groupadd csye6225

#add user to group
sudo useradd -s /usr/sbin/nologin -g csye6225 csye6225

pwd
ls -l
sudo cp ./webapp.service /etc/systemd/system

sudo chown -R csye6225:csye6225 /home/csye6225/webapp
sudo chmod 744 /home/csye6225/webapp

sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
