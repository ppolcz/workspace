@startuml

left to right direction
'top to bottom direction'

scale 3000*3000

package polcz.budget.session.* {

class MapEntityConverter << (B,orange) JSF, Request >> {
}

class AbstractMapEntityBean<T> {
    '- {static} serialVersionUID : long'
    -- used by JSF (selectOneItem) --
    - items : Map<String, T>
    '+ AbstractMapEntityBean(Class<?>)'
    '+ getItems() : T>'
}

AbstractMapEntityBean --|> AbstractEntityBean

class ChargeAccountBean << (B,orange) JSF, Session >> {
    '- {static} serialVersionUID : long'
    **@EJB //CAService//**
    '- service : CAService'
    --
    '+ ChargeAccountBean()'
    '# createNewEntity() : AbstractEntity'
    '# createNewEntity() : TChargeAccount'
    '+ getService() : TChargeAccount>'
}

ChargeAccountBean --|> AbstractMapEntityBean

class ClusterBean << (B,orange) JSF, Session >> {
    '- {static} serialVersionUID : long'
    **@EJB //ClusterService//**
    '- clservice : ClusterService'
    --
    '+ ClusterBean()'
    '# createNewEntity() : AbstractEntity'
    '# createNewEntity() : TCluster'
    '+ getService() : TCluster>'
}

ClusterBean --|> AbstractMapEntityBean

class MarketBean << (B,orange) JSF, Session >> {
    '- {static} serialVersionUID : long'
    **@EJB //MarketService//**
    '- service : MarketService'
    --
    '+ MarketBean()'
    '# createNewEntity() : AbstractEntity'
    '# createNewEntity() : TMarket'
    '+ getService() : TMarket>'
}

MarketBean --|> AbstractMapEntityBean

class TransactionBean << (B,orange) JSF, Session >> {
    '- {static} serialVersionUID : long'
    '- ID : int'
    **@EJB //StartupService//**
    **@EJB //TransactionService//**
    **@EJB //CAService//**
    **@EJB //MarketService//**
    **@EJB //ClusterService//**
    --
    '- ss : StartupService'
    '- trservice : TransactionService'
    '- caservice : CAService'
    '- mkservice : MarketService'
    '- clservice : ClusterService'
    - type : TTransactionType
    - old : TTransaction
    - filtering parameters
    '- filterCa : TChargeAccount'
    '- filterCatransfer : TChargeAccount'
    '- filterCluster : TCluster'
    '- filterMarket : TMarket'
    --
    '+ TransactionBean()'
    '+ getTypes() : TTransactionType;'
    '+ updateView() : String'
    '+ getOld() : TTransaction'
    '+ setOld(TTransaction) : void'
    '+ getFilterCa() : TChargeAccount'
    '+ setFilterCa(TChargeAccount) : void'
    '+ getFilterCluster() : TCluster'
    '+ setFilterCluster(TCluster) : void'
    '+ getFilterMarket() : TMarket'
    '+ setFilterMarket(TMarket) : void'
    '+ setType(TTransactionType) : void'
    '+ getFilterCatransfer() : TChargeAccount'
    '+ setFilterCatransfer(TChargeAccount) : void'
    '+ getService() : TTransaction>'
    '+ init() : void'
    '+ getType() : TTransactionType'
    '+ getID() : int'

    + updateList() **@Override** //szűrés miatt//
    - merge(TransactionArguments) : String
    - validate(TTransaction) : TTransaction

    -- called by JSF --
    + edit(TTransaction) **@Override**
    + create() **@Override**
    + persist() : String
    + merge() **@Override**
    + remove(TTransaction) **@Override**

    -- listeners --
    + onEdit(RowEditEvent) : void
    + onCancel(RowEditEvent) : void
}

TransactionBean --|> AbstractEntityBean

class AbstractEntityBean<T> {
    '- ID : int'
    '# logger : Logger'
    '+ init() : void'
    + updateList() : List<T> **@PostConstruct**
    # {abstract} createNewEntity() : T
    # initBeforeCreate() : void

    -- used by JSF --
    # entity : AbstractEntity (edit/create)
    # list : List (data table)

    -- called by JSF --
    + create() --> /create
    + edit(T) --> /edit
    + merge() [SQL insert/update] --> /view 
    + remove(T) [SQL remove] --> /view 

    '+ preRender(ComponentSystemEvent) : void'
    '+ change(ValueChangeEvent) : void'
    '+ ajaxListener(AjaxBehaviorEvent) : void'
    '+ actionListener(ActionEvent) : void'
    '+ getList() : List<T>'
    '+ submit() : String'
    '+ getEntity() : T'
    '+ setEntity(T) : void'
    '+ getID() : int'
}

AbstractEntityBean ..|> AbstractEntityBeanHelper

interface AbstractEntityBeanHelper<T,S> {
    + {abstract} getService() : S

    -- called by JSF --
    + {abstract} getList() : List<T>
}

'class MapEntityConverter << (B,orange) JSF, Request >> {                                       '
'    '~ logger : Logger'                                                                        '
'    **@ManagedProperty //MarketBean//**                                                        '
'    **@ManagedProperty //ClusterBean//**                                                       '
'    **@ManagedProperty //ChargeAccountBean//**                                                 '
'    '- mkBean : MarketBean'                                                                    '
'    '- clBean : ClusterBean'                                                                   '
'    '- caBean : ChargeAccountBean'                                                             '
'    --                                                                                         '
'    + getAsObject(..., String) : Object                                                        '
'    + getAsString(..., Object) : String                                                        '
'                                                                                               '
'    '+ MapEntityConverter()'                                                                   '
'    '+ getCaBean() : ChargeAccountBean'                                                        '
'    '+ getMkBean() : MarketBean'                                                               '
'    '+ setMkBean(MarketBean) : void'                                                           '
'    '+ getClBean() : ClusterBean'                                                              '
'    '+ setClBean(ClusterBean) : void'                                                          '
'    '+ setCaBean(ChargeAccountBean) : void'                                                    '
'    '- init() : void'                                                                          '
'}                                                                                              '
'                                                                                               '
'MapEntityConverter o.. MarketBean                                                              '
'MapEntityConverter o.. ClusterBean                                                             '
'MapEntityConverter o.. ChargeAccountBean                                                       '




class TransactionValidator << (B,orange) JSF, Request >> {
    **@EJB //StartupService//**

    --
    - cafrom : TChargeAccount
    - cafromInput : UIInput
    
    --
    + validateCluster(..., Object) : void
    + validateMarket(..., Object) : void
    + validateChargeAccountTo(..., Object) : void
    + validateChargeAccountFrom(..., Object) : void

    '+ TransactionValidator()'
    '+ validate(FacesContext, UIComponent, Object) : void'
    '- sendMessage(UIInput, boolean, FacesMessage) : void'
    '- validateChargeAccount(FacesContext, UIInput, Object, boolean) : void'
}

TransactionValidator -d- TransactionBean

}

@enduml
