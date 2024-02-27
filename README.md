1.Emulator device property:
   avd.ini.displayname              Pixel 2 API 34
   avd.ini.encoding                 UTF-8
   AvdId                            Pixel_2_API_34
   disk.dataPartition.size          6442450944
   fastboot.chosenSnapshotFile
   fastboot.forceChosenSnapshotBoot no
   fastboot.forceColdBoot           no
   fastboot.forceFastBoot           yes
   hw.accelerometer                 yes
   hw.arc                           false
   hw.audioInput                    yes
   hw.battery                       yes
   hw.camera.back                   virtualscene
   hw.camera.front                  emulated
   hw.cpu.ncore                     4
   hw.device.hash2                  MD5:55acbc835978f326788ed66a5cd4c9a7
   hw.device.manufacturer           Google
   hw.device.name                   pixel_2
   hw.dPad                          no
   hw.gps                           yes
   hw.gpu.enabled                   yes
   hw.gpu.mode                      auto
   hw.initialOrientation            Portrait
   hw.keyboard                      yes
   hw.lcd.density                   420
   hw.lcd.height                    1920
   hw.lcd.width                     1080
   hw.mainKeys                      no
   hw.ramSize                       1536
   hw.sdCard                        yes
   hw.sensors.orientation           yes
   hw.sensors.proximity             yes
   hw.trackBall                     no
   image.androidVersion.api         34
   image.sysdir.1                   system-images/android-34/google_apis_playstore/arm64-v8a/
   PlayStore.enabled                true
   runtime.network.latency          none
   runtime.network.speed            full
   showDeviceFrame                  yes
   skin.dynamic                     yes
   tag.display                      Google Play
   tag.id                           google_apis_playstore
   vm.heapSize                      256

2.In case the app doesn't run, change the sdk.dir line to your own in local.properties file under Gradle Script directory

3.User account credentials (username/password) that you have already created, through which we can log in:
   
   User1: uscid: 3563018064 password: 123123
   User2: uscid: 1111111111 password 123123
   User3: uscid: 2222222222 password: 123123
   User4: uscid: 3333333333 password: 123123
   User5: uscid: 4444444444 password: 123123
   Note: USC id must be a 10 digit number

4.Any specific problems you believe that we might face due to certain API restrictions (e.g. Firebase, Google Maps)
   Since our Firebase database was created with USC credentials, the app must operate under USC Wi-Fi or via USC VPN (vpn.usc.edu). 
   We apologize for any inconvenience.
   After registration, users will not be logged in automatically. The button in the top left corner allows you to return to the previous 
   page or to the map page, while the top right corner button navigates to the profile page. Users will need to log in again.
   Users also must log in to make reservations or view their profiles. On the profile page, users can directly edit their name and email. 
   On the map, reservation, and profile pages where scrolling is necessary, users must click and drag the screen to fully navigate through the pages.


Improved capabilities since Project 2.4：
Implemented photo upload for profiles
Implemented direct modifying of reservation in the profile tab
Modified Reservation Logic:
After the modification, the group allowed the user to select a 30-minute reservation within opening hours and a consecutive reservation up to 2 hours within opening hours. 
The group disallowed the user to select a reservation with non-consecutive slots or a reservation with longer than 2 hours or a reservation outside opening hours, or a reservation before current time.

