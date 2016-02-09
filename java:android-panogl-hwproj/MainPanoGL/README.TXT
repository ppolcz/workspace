=================================================================================================================
================================================= CHP 2 =========================================================
=================================================================================================================

Kontribuciok:
> PanoramaCreatorActivity:
    - sikerult atlatszo texturat generalni, amelynek segitsegevel 
    (a GLSurfaceView atlatszosaga miatt) a textura alol lathatova
    valik a CameraPreview.
    - lehet fenykepezni
    - fenykepezes utan a fenykepezett kep egy masodpercig lathato,
    majd ujra elindul a preview (EBBEN AZ EGY PERCBEN MEG NE NYOMD
    MEG A GOMBOT UJRA, MERT LEFAGY - ezt majd kidebuggolom)
    - sikerult beintegralnom a Panodroid panorama nezegetojet: az
    album gombot megnyomva megjelenik egy letoltott panorama kep egy 
    GL kocka texturajakent - teljesen olyan hatasu mintha ott hely-
    ben nezne az ember a camera preview-t.
  
> Design:  
    Beres Dora gombjait egy kicsit meg 9patch-eltem es
    behelyeztem a CameraPreview es a GLSurfaceView fole.
    ... Launcher icon is ... 

> Gallery nezegeto: (ezzel kezd az applikacio)
    - ListView + ViewPager + SQLite adatbazis
    - ViewPagerbe belevan szurva nehany demo kep, amelyek:
        sdcard/NAT/bird-wallpaper-1600x900-009.jpg
        sdcard/NAT/bird-wallpaper-1600x900-010.jpg
        sdcard/NAT/bird-wallpaper-1600x900-011.jpg
        sdcard/NAT/bird-wallpaper-1600x900-012.jpg
    Ha ezek a kepek nincsenek a fajlrendszer adott helyein, akkor 
    force close-ol. - ezeket a kepeket egy kulon zipben elkuldok,
    amit szukseg eseten hasznalhattok.
    - hogy ne kelljen sokat gepelni:
    adb shell mkdir sdcard/NAT ; \
    adb shell bird-wallpaper-1600x900-009.jpg sdcard/NAT ; \
    adb shell bird-wallpaper-1600x900-010.jpg sdcard/NAT ; \
    adb shell bird-wallpaper-1600x900-011.jpg sdcard/NAT ; \
    adb shell bird-wallpaper-1600x900-012.jpg sdcard/NAT ;


-----------------------------------------------------------------------------------------

Megjegyzes:
    Helyenkent meg elofordulnak OutOfMemoryException-ok, es meg kitudja mik...
    Ertelem szeruen ezeken a legjobb tudasom szerint javitani fogok.

-----------------------------------------------------------------------------------------

Problemak: 
    - a panodroid texturazott negyszoget nem sikerult meg rendesen
    beleraknom a fenykepezo aktivitybe, mert minden eltakar.
    akkor is ha a setMediaOverlay(true) -ra van allitva.
    - a panodroid es a hozza tartozok osztalyok eleg erosen enkapszulaltak 
    igy nem lesz kis munka kibogaraszni, hogy miert is viselkedik ugy ahogy 
    nem szeretnem...

-----------------------------------------------------------------------------------------

Egyeb megjegyzes:
    - a Camera API-val tobb baj van mint egy novel.

=================================================================================================================
================================================= CHP 3 =========================================================
=================================================================================================================

Javitasok:
    - CreatePanoramaActivity handleret atirtam outerClass -a,
    mert igy nem lep fel a potencialis memory leak lehetosege.
    - DataSource, DBHelper - mind singletonok es szinkronizaltak lettek.
    Java synchornization tekinteteben keves tapasztalatom van, 
    a singleton szinkronizaciojat pusztan jozan paraszt eszem szerint vegeztem.
    - nem hal meg ha egymas utan ketszer megnyomom a shoot gombot (mert arra kis idore
    amig lathato az elkeszitett kep disabled allapotba van teve a gomb)

    - A ViewerActivity kezdi felvenni vegleges alakjat - csak a FullscreenViewer hianyzik meg.


E/AndroidRuntime: java.lang.NullPointerException
E/AndroidRuntime:       at com.polpe.panogl.view.GalleryListActivity.positionChanged(GalleryListActivity.java:358)
E/AndroidRuntime:       at com.polpe.panogl.view.ImagePagerAdapter.setPrimaryItem(ImagePagerAdapter.java:55)


=================================================================================================================
================================================= CHP 4 =========================================================
=================================================================================================================

Kontribuciok:
> Gallery nezegeto:
    - ListView-Preview nezegeto stabil, nem hal meg.
    Ha nincs kep egy adott panoramaban, akkor felajanla a lehetoseget, hogy toroljem.
    - Torles siman megy.

> Fullscreen nezegeto:
    info.androidhive -rol letoltott Activity-t hasznalom, es jol mukodik.

> SQLite:
    letrehoztam az images tablat is, mely a panoramas kulcsara hivatkozik.

> PanoramaGL
    VEGRE SIKERULT: 
    - kiderult, hogy az ahogyan regen csinaltam (SurfaceView::setZOrder...) az nem jo.
    Hosszas dokumentacio olvasas utan rajottem, hogyan kell!
    - a panoramaGL-t elkezdtem ujrairni egy sajat keszitesu renderer segitsegevel,
    ugyanis az OpenPanodroid rendererjet, nem tudom atlatszosagra birni.
    LASD: MENU
    - elso demo: OpenPanodroid, ami nagyon fancy-n forog de nem atlatszo
    - masodik demo: sajat GL renderer, nagyon szarul forog de atlatszo

> TODO
    - az OpenPanodroid rendererjenek forgatasait es nativ hivasait bele kellene irni az en rendererembe es akkor jo lesz
    - lasd TODO/TODO.txt



E/EmbeddedLogger: App crashed! Process: com.polpe.panogl
E/EmbeddedLogger: App crashed! Package: com.polpe.panogl v1 (1.0)
E/AndroidRuntime: FATAL EXCEPTION: main
---- FONTOS -----------------------------------------------------
E/AndroidRuntime: java.lang.NullPointerException
E/AndroidRuntime:       at com.polpe.panogl.demo.MainHandler.handleMessage(GLDemoActivity.java:480)
E/AndroidRuntime:       at android.os.Handler.dispatchMessage(Handler.java:99)
E/AndroidRuntime:       at android.os.Looper.loop(Looper.java:155)
E/AndroidRuntime:       at android.app.ActivityThread.main(ActivityThread.java:5536)
E/AndroidRuntime:       at java.lang.reflect.Method.invokeNative(Native Method)
E/AndroidRuntime:       at java.lang.reflect.Method.invoke(Method.java:511)
E/AndroidRuntime:       at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1074)
E/AndroidRuntime:       at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:841)
E/AndroidRuntime:       at dalvik.system.NativeStart.main(Native Method)
E/EmbeddedLogger: Application Label: TranslucentGLSurfaceView
E/PhoneStatusBar: mDisplayMetrics ,width: 800,heigh: 480, getRotation: 0, btn_params.width94
E/InputDispatcher: channel '432ccf30 com.polpe.panogl/com.polpe.panogl.view.GalleryListActivity (server)' ~ Channel is unrecoverably broken and will be disposed!
E/Trace   : error opening trace file: No such file or directory (2)
E/PhoneStatusBar: mDisplayMetrics ,width: 480,heigh: 800, getRotation: 1, btn_params.width158
E/mm-camera: mctl_init_rawchip_proc_info: sensor_get_params failed -1
E/mm-libcamera2: kuei, MM_CAMERA_PARM_QTR_SIZE_PREVIEW
E/mm-camera: camif_client_set_params: camif has associated with obj mask 0x1
E/mm-camera: config_MSG_ID_RESET_ACK CAMIF_PARAMS_ADD_OBJ_ID failed -1 
E/PhoneStatusBar: mDisplayMetrics ,width: 800,heigh: 480, getRotation: 0, btn_params.width94
E/PhoneStatusBar: mDisplayMetrics ,width: 480,heigh: 800, getRotation: 1, btn_params.width158
E/mm-camera: mctl_init_rawchip_proc_info: sensor_get_params failed -1
E/mm-libcamera2: kuei, MM_CAMERA_PARM_QTR_SIZE_PREVIEW
E/mm-camera: camif_client_set_params: camif has associated with obj mask 0x1
E/mm-camera: config_MSG_ID_RESET_ACK CAMIF_PARAMS_ADD_OBJ_ID failed -1 
E/mm-camera: mctl_init_rawchip_proc_info: sensor_get_params failed -1
E/mm-libcamera2: kuei, MM_CAMERA_PARM_QTR_SIZE_PREVIEW
E/mm-camera: camif_client_set_params: camif has associated with obj mask 0x1
E/mm-camera: config_MSG_ID_RESET_ACK CAMIF_PARAMS_ADD_OBJ_ID failed -1 
E/AndroidRuntime: FATAL EXCEPTION: GLThread 3575
E/AndroidRuntime: java.lang.IllegalArgumentException: bitmap is recycled
E/AndroidRuntime:       at android.opengl.GLUtils.texImage2D(GLUtils.java:160)
E/AndroidRuntime:       at com.polpe.panogl.demo.PhotoCube.loadTexture(PhotoCube.java:160)
E/AndroidRuntime:       at com.polpe.panogl.demo.MyGLRenderer.onSurfaceCreated(MyGLRenderer.java:46)
E/AndroidRuntime:       at android.opengl.GLSurfaceView$GLThread.guardedRun(GLSurfaceView.java:1494)
E/AndroidRuntime:       at android.opengl.GLSurfaceView$GLThread.run(GLSurfaceView.java:1240)
E/EmbeddedLogger: App crashed! Process: com.polpe.panogl
E/EmbeddedLogger: App crashed! Package: com.polpe.panogl v1 (1.0)
E/EmbeddedLogger: Application Label: TranslucentGLSurfaceView
E/PhoneStatusBar: mDisplayMetrics ,width: 800,heigh: 480, getRotation: 0, b

=================================================================================================================
================================================= CHP 5 =========================================================
=================================================================================================================

Contributions:
> z-rotation
	- the whole model (textured cube including its latitude, longitude rotations)
	can be rotated by z-axis
> Sensor-controlled rotation
	- HTC Desire 500 phones do not have magnetic sensors nor gyroscope,
	thus azimuth values have not been detected yet
