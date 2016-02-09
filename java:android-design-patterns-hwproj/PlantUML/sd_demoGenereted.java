@startuml

[--> Main : start
activate Main
Main --> Main : new Main()
activate Main
Main --> DataStore : new DataStore()
deactivate Main
activate DataStore
DataStore --> AbstractDataStore : new AbstractDataStore()
deactivate DataStore
activate AbstractDataStore
AbstractDataStore --> DataStore : getHelperInstance()
deactivate AbstractDataStore
activate DataStore
AbstractDataStore <-- DataStore : return
deactivate DataStore
activate AbstractDataStore
DataStore <-- AbstractDataStore : return
deactivate AbstractDataStore
activate DataStore
Main <-- DataStore : return
deactivate DataStore
activate Main
Main <-- Main : return
deactivate Main
[<--Main : end
deactivate Main

[--> Main : start
activate Main
Main --> Main : new Main()
activate Main
Main --> AbstractDataStore : onCreate()
deactivate Main
activate AbstractDataStore
AbstractDataStore --> SQLiteDriverJDBC : onCreate()
deactivate AbstractDataStore
activate SQLiteDriverJDBC
SQLiteDriverJDBC --> SQLiteDriverJDBC : onOpen()
activate SQLiteDriverJDBC
SQLiteDriverJDBC <-- SQLiteDriverJDBC : return
deactivate SQLiteDriverJDBC
AbstractDataStore <-- SQLiteDriverJDBC : return
deactivate SQLiteDriverJDBC
activate AbstractDataStore
Main <-- AbstractDataStore : return
deactivate AbstractDataStore
activate Main
Main <-- Main : return
deactivate Main
[<--Main : end
deactivate Main

@enduml