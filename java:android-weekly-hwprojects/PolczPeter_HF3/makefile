
# android create project \
# --target <target_ID> \
# --name <your_project_name> \
# --path path/to/your/project \
# --activity <your_activity_name> \
# --package <your_package_namespace>

# android create project \
# --target 1 \
# --name MyAndroidApp \
# --path ./MyAndroidAppProject \
# --activity MyAndroidAppActivity \
# --package com.example.myandroid

# android list targets

ANDROID = /home/polcz/Desktop/adt-bundle-linux-x86-20130729/sdk/tools/android 


all: build.xml
	ant debug
	adb install bin`python getname.py`.apk

build.xml:
	$(ANDROID) update project --name `python getname.py` --target 4 --path .

clean:
	rm build.xml	