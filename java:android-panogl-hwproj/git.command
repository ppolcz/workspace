git remote add -f MainPanoGL ../TranslucentGLSurfaceView
git merge -s ours --no-commit MainPanoGL/master 
git read-tree --prefix=MainPanoGL -u MainPanoGL/master
git commit -m "Merge MainPanoGL project into PolpePanoGL"
git pull -s subtree MainPanoGL master 

git remote add -f SensorTest ../PolczPeter_Accelerometer
git merge -s ours --no-commit SensorTest/master 
git read-tree --prefix=SensorTest -u SensorTest/master
git commit -m "Merge SensorTest project into PolpePanoGL"
git pull -s subtree SensorTest master 

