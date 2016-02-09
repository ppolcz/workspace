@startuml

left to right direction

scale 3000*3000

package "service (EJB3 beans)" as service {

hide AbstractService fields
abstract class AbstractService<T> << admin, customer >> {
    + find(Object) : T
    + findAll() : List<T>
    + create(T) : void
    + edit(T) : void
    + remove(T) : void
}

hide CAService methods
class CAService << (B,orchid) admin >> {
    - em : EntityManager
}

CAService --|> AbstractService

hide ClusterService methods
class ClusterService << (B,orchid) admin, customer >> {
    - em : EntityManager
}

ClusterService --|> AbstractService

hide MarketService methods
class MarketService << (B,orchid) admin, customer >> {
    - em : EntityManager
}

MarketService --|> AbstractService


class TransactionService << (B,orchid) admin, customer >> {
    - em : EntityManager
    - find(SelectByArguments)
    -- Transactional --
    + makeTransaction(TransactionArguments)
    + edit(TTransaction) : void //(guilt-free)//
}

TransactionService --|> AbstractService

hide TransactionArguments methods
class TransactionArguments {
    - acttr : TTransaction
    - oldtr : TTransaction
    - type : int
}


hide TransactionUpdater fields
class TransactionUpdater {
    + execute() : void
    - recalculate(List<TTransaction>)
    - findDirtyElements(Date, Date, TChargeAccount)
}

TransactionService -l- TransactionUpdater
TransactionService -d- TransactionArguments
TransactionUpdater -d- TransactionArguments

}

@enduml