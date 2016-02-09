@startuml

left to right direction

scale 3000*3000

abstract class AbstractEntity {
    '- {static} serialVersionUID : long'
    '+ AbstractEntity()'
    
    + {abstract} getUid() : int
    + {abstract} setUid(int) : void
    --
    + equals(Object) : boolean
    + hashCode() : int
}

abstract class AbstractNameDescEntity {
    '- {static} serialVersionUID : long'
    '+ AbstractNameDescEntity()'
    
    + {abstract} getDesc() : String
    + {abstract} setDesc(String) : void
    + {abstract} getName() : String
    + {abstract} setName(String) : void
    --
    + toString() : String
}

AbstractNameDescEntity --|> AbstractEntity

class TChargeAccount << (E,yellow) Entity >> {
    '- {static} serialVersionUID : long'
    # uid : int
    - desc : String
    - name : String
    - TTransactions : List
    --
    '+ TChargeAccount(String, String)'
    '+ TChargeAccount(String)'
    '+ TChargeAccount()'
    '+ getDesc() : String'
    '+ setDesc(String) : void'
    '+ getUid() : int'
    '+ setUid(int) : void'
    '+ getTTransactions() : TTransaction>'
    '+ setTTransactions(List<TTransaction>) : void'
    '+ addTTransaction(TTransaction) : TTransaction'
    '+ removeTTransaction(TTransaction) : TTransaction'
    '+ getName() : String'
    '+ setName(String) : void'
}

TChargeAccount --|> AbstractNameDescEntity

class TCluster << (E,yellow) Entity >> {
    '- {static} serialVersionUID : long'
    # uid : int
    # desc : String
    # name : String
    - sgn : int
    - parent : TCluster
    - children : List
    - pis : List
    - trs : List
    --
    '+ TCluster(String, int, TCluster)'
    '+ TCluster(String, TCluster)'
    '+ TCluster(String)'
    '+ TCluster()'
    '+ getDesc() : String'
    '+ setDesc(String) : void'
    '+ getUid() : int'
    '+ setUid(int) : void'
    '+ getSgn() : int'
    '+ setSgn(int) : void'
    '+ getTClusters() : TCluster>'
    '+ setChildren(List<TCluster>) : void'
    '+ addChild(TCluster) : TCluster'
    '+ removeChild(TCluster) : TCluster'
    '+ getTProductInfos() : TProductInfo>'
    '+ setTProductInfos(List<TProductInfo>) : void'
    '+ addTProductInfo(TProductInfo) : TProductInfo'
    '+ removeTProductInfo(TProductInfo) : TProductInfo'
    '+ getTTransactions() : TTransaction>'
    '+ setTTransactions(List<TTransaction>) : void'
    '+ addTTransaction(TTransaction) : TTransaction'
    '+ removeTTransaction(TTransaction) : TTransaction'
    + equals(TCluster) : boolean
    '+ getName() : String'
    '+ getParent() : TCluster'
    '+ setName(String) : void'
    '+ setParent(TCluster) : void'
}

TCluster --|> AbstractNameDescEntity

class TMarket << (E,yellow) Entity >> {
    '- {static} serialVersionUID : long'
    # uid : int
    # desc : String
    # name : String
    - TProductInfos : List
    - trs : List
    --
    '+ TMarket()'
    '+ TMarket(String, String)'
    '+ getDesc() : String'
    '+ setDesc(String) : void'
    '+ getUid() : int'
    '+ setUid(int) : void'
    '+ getTProductInfos() : TProductInfo>'
    '+ setTProductInfos(List<TProductInfo>) : void'
    '+ addTProductInfo(TProductInfo) : TProductInfo'
    '+ removeTProductInfo(TProductInfo) : TProductInfo'
    '+ getTTransactions() : TTransaction>'
    '+ setTTransactions(List<TTransaction>) : void'
    '+ addTTransaction(TTransaction) : TTransaction'
    '+ removeTTransaction(TTransaction) : TTransaction'
    '+ getName() : String'
    '+ setName(String) : void'
}

TMarket --|> AbstractNameDescEntity

class TTransaction << (E,yellow) Entity >> {
    '- {static} serialVersionUID : long'
    - uid : int
    - amount : int
    - balance : int
    - date : Date
    - remark : String
    - pivot : boolean
    - pis : List
    - ca : TChargeAccount
    - catransfer : TChargeAccount
    - cluster : TCluster
    - market : TMarket
    --
    '+ TTransaction(TChargeAccount, TChargeAccount, TCluster, TMarket)'
    '+ TTransaction()'
    '+ TTransaction(int, int, Date, String, TChargeAccount, TCluster, TMarket, boolean)'
    '+ getDate() : Date'
    '+ getUid() : int'
    '+ setUid(int) : void'
    '+ getCa() : TChargeAccount'
    '+ getCatransfer() : TChargeAccount'
    '+ getMarket() : TMarket'
    '+ getCluster() : TCluster'
    '+ isPivot() : boolean'
    '+ setDate(Date) : void'
    '+ setCa(TChargeAccount) : void'
    '+ setCatransfer(TChargeAccount) : void'
    '+ setPivot(boolean) : void'
    '+ setAmount(int) : void'
    '+ setCluster(TCluster) : void'
    '+ setMarket(TMarket) : void'
    '+ setBalance(int) : void'
    '+ getRemark() : String'
    '+ getTProductInfos() : TProductInfo>'
    '+ setTProductInfos(List<TProductInfo>) : void'
    '+ addTProductInfo(TProductInfo) : TProductInfo'
    '+ removeTProductInfo(TProductInfo) : TProductInfo'
    '+ getAmount() : int'
    '+ getBalance() : int'
    '+ setRemark(String) : void'
    + toString() : String
}

TTransaction --|> AbstractEntity

'hide TTransactionType methods'
'enum TTransactionType {'
'    **simple**'
'    **transfer**'
'    **pivot**'
'    '- {static} ENUM$VALUES : TTransactionType;''
'    '--''
'    '+ {static} values() : TTransactionType;''
'    '+ {static} valueOf(String) : TTransactionType''
'    '+ getName() : String''
'}'

MapEntityConverter -r- AbstractNameDescEntity
class MapEntityConverter << (B,orange) JSF, Request >> {
    '~ logger : Logger'
    **@ManagedProperty //MarketBean//**
    **@ManagedProperty //ClusterBean//**
    **@ManagedProperty //ChargeAccountBean//**
    '- mkBean : MarketBean'
    '- clBean : ClusterBean'
    '- caBean : ChargeAccountBean'
    --
    + getAsObject(..., String) : Object
    + getAsString(..., Object) : String
    
    '+ MapEntityConverter()'
    '+ getCaBean() : ChargeAccountBean'
    '+ getMkBean() : MarketBean'
    '+ setMkBean(MarketBean) : void'
    '+ getClBean() : ClusterBean'
    '+ setClBean(ClusterBean) : void'
    '+ setCaBean(ChargeAccountBean) : void'
    '- init() : void'
}


@enduml