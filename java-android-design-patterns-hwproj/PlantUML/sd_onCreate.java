@startuml

participant "MainActivity" as MA
participant "DataStore" as DS
participant "MySQLiteHelper" as HP
participant "MySQLiteOpenHelper" as OHP

[--> MA : onCreate()
group MainActivity.onCreate()
'== MainActivity.onCreate() =='
activate MA
MA --> MA : setContentView()
MA --> DS : init instance
deactivate MA
activate DS
DS --> DS : @override\ngetHelperInstance()
activate DS
DS --> HP : init instance 
deactivate DS
DS --> HP : setSqlInterface(this.sql)
MA <-- DS : instance
deactivate DS
activate MA
MA --> DS : setContext(this)
DS --> HP : setContext(context)
MA --> DS : onCreate()
deactivate MA
activate DS
alt not created yet
DS --> HP : onCreate()
deactivate DS
activate HP
HP <-- OHP : new MySQLiteOpenHelper
HP --> OHP : setContext(context)
HP --> OHP : setSQLCommands(sql)
HP --> OHP : getWritableDatabase()
deactivate HP
activate OHP
OHP --> OHP : ...
HP <-- OHP : db
deactivate OHP
activate HP
HP --> OHP : onCreate(db)
deactivate HP
activate OHP
OHP --> OHP : creating tables
DS <-- OHP
deactivate OHP
activate DS
DS --> DS : this.sql.initAfterCreate()\n//insert defaults//
end
MA <-- DS
deactivate DS
activate MA
...
end
[<-- MA 
deactivate MA
deactivate DS
deactivate MA

... application is running: select, insert, update, delete ...

[--> MA : onDestroy()
group onDestroy()
MA --> DS : onDestroy()
alt is created
DS --> HP : onDestroy()
HP --> HP : db.close()
HP --> OHP : close()
end
end

@enduml