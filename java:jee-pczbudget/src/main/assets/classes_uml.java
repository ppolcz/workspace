@startuml

scale 3000*3000

class AbstractEntity {
    - {static} serialVersionUID : long
    --
    + AbstractEntity()
    + {abstract} getUid() : int
    + {abstract} setUid(int) : void
    + equals(Object) : boolean
    + hashCode() : int
}

class AbstractMapEntityBean {
    - {static} serialVersionUID : long
    - items : Map
    --
    + AbstractMapEntityBean(Class<?>)
    + getItems() : T>
}

AbstractMapEntityBean ..|> Serializable
AbstractMapEntityBean --|> AbstractEntityBean

class ChargeAccountBean {
    - {static} serialVersionUID : long
    - service : CAService
    --
    + ChargeAccountBean()
    # createNewEntity() : AbstractEntity
    # createNewEntity() : TChargeAccount
    + getService() : TChargeAccount>
}

ChargeAccountBean ..|> Serializable
ChargeAccountBean --|> AbstractMapEntityBean

class ClusterBean {
    - {static} serialVersionUID : long
    - clservice : ClusterService
    --
    + ClusterBean()
    # createNewEntity() : AbstractEntity
    # createNewEntity() : TCluster
    + getService() : TCluster>
}

ClusterBean ..|> Serializable
ClusterBean --|> AbstractMapEntityBean

class MarketBean {
    - {static} serialVersionUID : long
    - service : MarketService
    --
    + MarketBean()
    # createNewEntity() : AbstractEntity
    # createNewEntity() : TMarket
    + getService() : TMarket>
}

MarketBean ..|> Serializable
MarketBean --|> AbstractMapEntityBean

class TransactionBean {
    - {static} serialVersionUID : long
    - ID : int
    - ss : StartupService
    - trservice : TransactionService
    - caservice : CAService
    - mkservice : MarketService
    - clservice : ClusterService
    - type : TTransactionType
    - old : TTransaction
    - filterCa : TChargeAccount
    - filterCatransfer : TChargeAccount
    - filterCluster : TCluster
    - filterMarket : TMarket
    --
    + TransactionBean()
    # createNewEntity() : TTransaction
    # createNewEntity() : AbstractEntity
    + edit(TTransaction) : String
    + edit(AbstractEntity) : String
    + updateList() : TTransaction>
    + getTypes() : TTransactionType;
    + updateView() : String
    - validate(TTransaction) : TTransaction
    + persist() : String
    + onEdit(RowEditEvent) : void
    + onCancel(RowEditEvent) : void
    + getOld() : TTransaction
    + setOld(TTransaction) : void
    + getFilterCa() : TChargeAccount
    + setFilterCa(TChargeAccount) : void
    + getFilterCluster() : TCluster
    + setFilterCluster(TCluster) : void
    + getFilterMarket() : TMarket
    + setFilterMarket(TMarket) : void
    + setType(TTransactionType) : void
    + getFilterCatransfer() : TChargeAccount
    + setFilterCatransfer(TChargeAccount) : void
    + getService() : TTransaction>
    + remove(AbstractEntity) : String
    + remove(TTransaction) : String
    + init() : void
    + create() : String
    - merge(TransactionArguments) : String
    + merge() : String
    + getType() : TTransactionType
    + getID() : int
}

TransactionBean ..|> Serializable
TransactionBean --|> AbstractEntityBean

class AbstractEntityBean {
    - ID : int
    # logger : Logger
    # entity : AbstractEntity
    # list : List
    --
    # {abstract} createNewEntity() : T
    # initBeforeCreate() : void
    + edit(T) : String
    + updateList() : List<T>
    + preRender(ComponentSystemEvent) : void
    + change(ValueChangeEvent) : void
    + ajaxListener(AjaxBehaviorEvent) : void
    + actionListener(ActionEvent) : void
    + getList() : List<T>
    + submit() : String
    + getEntity() : T
    + setEntity(T) : void
    + remove(T) : String
    + init() : void
    + create() : String
    + merge() : String
    + getID() : int
}

AbstractEntityBean ..|> AbstractEntityBeanHelper

interface AbstractEntityBeanHelper {
    --
    + {abstract} getList() : List<T>
    + {abstract} getService() : S
}


class AbstractValidator {
    ~ logger : Logger
    --
    + AbstractValidator(Class<?>)
}

AbstractValidator ..|> Validator

class TransactionValidator {
    ~ ss : StartupService
    - cafrom : TChargeAccount
    - cafromInput : UIInput
    --
    + TransactionValidator()
    + validate(FacesContext, UIComponent, Object) : void
    + validateCluster(FacesContext, UIComponent, Object) : void
    + validateMarket(FacesContext, UIComponent, Object) : void
    - sendMessage(UIInput, boolean, FacesMessage) : void
    + validateChargeAccountTo(FacesContext, UIComponent, Object) : void
    + validateChargeAccountFrom(FacesContext, UIComponent, Object) : void
    - validateChargeAccount(FacesContext, UIInput, Object, boolean) : void
}

TransactionValidator --|> AbstractValidator

class AbstractNameDescEntity {
    - {static} serialVersionUID : long
    --
    + AbstractNameDescEntity()
    + {abstract} getDesc() : String
    + {abstract} setDesc(String) : void
    + toString() : String
    + {abstract} getName() : String
    + {abstract} setName(String) : void
}

AbstractNameDescEntity --|> AbstractEntity

class TChargeAccount {
    - {static} serialVersionUID : long
    # uid : int
    - desc : String
    - name : String
    - TTransactions : List
    --
    + TChargeAccount(String, String)
    + TChargeAccount(String)
    + TChargeAccount()
    + getDesc() : String
    + setDesc(String) : void
    + getUid() : int
    + setUid(int) : void
    + getTTransactions() : TTransaction>
    + setTTransactions(List<TTransaction>) : void
    + addTTransaction(TTransaction) : TTransaction
    + removeTTransaction(TTransaction) : TTransaction
    + getName() : String
    + setName(String) : void
}

TChargeAccount --|> AbstractNameDescEntity

class TCluster {
    - {static} serialVersionUID : long
    # uid : int
    # desc : String
    # name : String
    - sgn : int
    - parent : TCluster
    - children : List
    - pis : List
    - trs : List
    --
    + TCluster(String, int, TCluster)
    + TCluster(String, TCluster)
    + TCluster(String)
    + TCluster()
    + getDesc() : String
    + setDesc(String) : void
    + getUid() : int
    + setUid(int) : void
    + getSgn() : int
    + setSgn(int) : void
    + getTClusters() : TCluster>
    + setChildren(List<TCluster>) : void
    + addChild(TCluster) : TCluster
    + removeChild(TCluster) : TCluster
    + getTProductInfos() : TProductInfo>
    + setTProductInfos(List<TProductInfo>) : void
    + addTProductInfo(TProductInfo) : TProductInfo
    + removeTProductInfo(TProductInfo) : TProductInfo
    + getTTransactions() : TTransaction>
    + setTTransactions(List<TTransaction>) : void
    + addTTransaction(TTransaction) : TTransaction
    + removeTTransaction(TTransaction) : TTransaction
    + equals(TCluster) : boolean
    + getName() : String
    + getParent() : TCluster
    + setName(String) : void
    + setParent(TCluster) : void
}

TCluster --|> AbstractNameDescEntity

class TMarket {
    - {static} serialVersionUID : long
    # uid : int
    # desc : String
    # name : String
    - TProductInfos : List
    - trs : List
    --
    + TMarket()
    + TMarket(String, String)
    + getDesc() : String
    + setDesc(String) : void
    + getUid() : int
    + setUid(int) : void
    + getTProductInfos() : TProductInfo>
    + setTProductInfos(List<TProductInfo>) : void
    + addTProductInfo(TProductInfo) : TProductInfo
    + removeTProductInfo(TProductInfo) : TProductInfo
    + getTTransactions() : TTransaction>
    + setTTransactions(List<TTransaction>) : void
    + addTTransaction(TTransaction) : TTransaction
    + removeTTransaction(TTransaction) : TTransaction
    + getName() : String
    + setName(String) : void
}

TMarket --|> AbstractNameDescEntity

class TTransaction {
    - {static} serialVersionUID : long
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
    + TTransaction(TChargeAccount, TChargeAccount, TCluster, TMarket)
    + TTransaction()
    + TTransaction(int, int, Date, String, TChargeAccount, TCluster, TMarket, boolean)
    + getDate() : Date
    + getUid() : int
    + setUid(int) : void
    + getCa() : TChargeAccount
    + getCatransfer() : TChargeAccount
    + getMarket() : TMarket
    + getCluster() : TCluster
    + isPivot() : boolean
    + setDate(Date) : void
    + setCa(TChargeAccount) : void
    + setCatransfer(TChargeAccount) : void
    + setPivot(boolean) : void
    + setAmount(int) : void
    + setCluster(TCluster) : void
    + setMarket(TMarket) : void
    + setBalance(int) : void
    + getRemark() : String
    + getTProductInfos() : TProductInfo>
    + setTProductInfos(List<TProductInfo>) : void
    + addTProductInfo(TProductInfo) : TProductInfo
    + removeTProductInfo(TProductInfo) : TProductInfo
    + getAmount() : int
    + getBalance() : int
    + setRemark(String) : void
    + toString() : String
}

TTransaction --|> AbstractEntity

enum TTransactionType {
    simple
    transfer
    pivot
    - {static} ENUM$VALUES : TTransactionType;
    --
    + {static} values() : TTransactionType;
    + {static} valueOf(String) : TTransactionType
    + getName() : String
}


class MapEntityConverter {
    ~ logger : Logger
    - mkBean : MarketBean
    - clBean : ClusterBean
    - caBean : ChargeAccountBean
    --
    + MapEntityConverter()
    + getAsObject(FacesContext, UIComponent, String) : Object
    + getCaBean() : ChargeAccountBean
    + getAsString(FacesContext, UIComponent, Object) : String
    + getMkBean() : MarketBean
    + setMkBean(MarketBean) : void
    + getClBean() : ClusterBean
    + setClBean(ClusterBean) : void
    + setCaBean(ChargeAccountBean) : void
    - init() : void
}

MapEntityConverter ..|> Converter

class AbstractService {
    - entityClass : Class
    # logger : Logger
    --
    + AbstractService(Class<T>)
    + edit(T) : void
    + findAll() : List<T>
    # {abstract} em() : EntityManager
    + findRange([I) : List<T>
    + remove(T) : void
    + count() : int
    + find(Object) : T
    + create(T) : void
}


class CAService {
    - em : EntityManager
    --
    + CAService()
    + edit(TChargeAccount) : void
    + edit(Object) : void
    # em() : EntityManager
    + remove(Object) : void
    + remove(TChargeAccount) : void
    + create(TChargeAccount) : void
    + create(Object) : void
}

CAService --|> AbstractService

class ClusterService {
    - em : EntityManager
    --
    + ClusterService()
    # em() : EntityManager
}

ClusterService --|> AbstractService

class MarketService {
    - em : EntityManager
    --
    + MarketService()
    # em() : EntityManager
}

MarketService --|> AbstractService

class StartupService {
    ~ em : EntityManager
    ~ logger : Logger
    - Nem_Adott : TCluster
    - Napi_Szukseglet : TCluster
    - Szamolas : TCluster
    - Athelyezes : TCluster
    - Market_Not_Applicable : TMarket
    - none : TChargeAccount
    - pkez : TChargeAccount
    --
    + StartupService()
    + none() : TChargeAccount
    + pkez() : TChargeAccount
    + Napi_Szukseglet() : TCluster
    + Market_Not_Applicable() : TMarket
    + Szamolas() : TCluster
    + Athelyezes() : TCluster
    - ca(TChargeAccount) : TChargeAccount
    - cluster(TCluster) : TCluster
    - market(TMarket) : TMarket
    + Nem_Adott() : TCluster
    - findOrCreate(Class<T>, T) : T
    - generateClusters() : void
    + init() : void
}


class TransactionService {
    - em : EntityManager
    ~ ss : StartupService
    - utx : UserTransaction
    --
    + TransactionService()
    + edit(Object) : void
    + edit(TTransaction) : void
    + makeTransaction(TransactionArguments) : boolean
    + findFirstSimpleTransactionBefore(TTransaction) : TTransaction
    + findAll(TChargeAccount, TChargeAccount, TCluster, TMarket) : TTransaction>
    # em() : EntityManager
    - executeTransaction(TransactionArguments) : void
    - makeRollback() : boolean
    - findSingleOrNull(TypedQuery<T>) : T
    + findLastPivotBefore(TTransaction) : TTransaction
    + findLastPivotBefore(Date, TChargeAccount) : TTransaction
    + findFirstPivotAfter(TTransaction) : TTransaction
    + findFirstPivotAfter(Date, TChargeAccount) : TTransaction
    + findElementsBetween(Date, Date, TChargeAccount) : TTransaction>
    - find(TransactionService$SelectByArguments) : TTransaction>
}

TransactionService --|> AbstractService

class TransactionArguments {
    - acttr : TTransaction
    - oldtr : TTransaction
    - type : int
    --
    + TransactionArguments(TTransaction, TTransaction, int)
    + validate() : void
    + setType(int) : void
    + setActtr(TTransaction) : void
    + getActtr() : TTransaction
    + getOldtr() : TTransaction
    + setOldtr(TTransaction) : void
    + getType() : int
}


class TransactionUpdater {
    - none : TChargeAccount
    - athelyezes : TCluster
    - logger : Logger
    - service : TransactionService
    - args : TransactionArguments
    --
    + TransactionUpdater(TransactionService, Logger, TransactionArguments, TCluster, TChargeAccount)
    - recalculate(List<TTransaction>) : TransactionUpdater
    - findDirtyElements(Date, Date, TChargeAccount) : TTransaction>
    - updateDirtyElements(Date, Date, TChargeAccount) : void
    # log(TTransaction) : void
    # log(String, Object[]) : void
    + execute() : void
}

@enduml