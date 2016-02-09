@startuml

class Analyser {
    Ez az osztály elemzi a költekezés szokásainkat, kiadásainkat.
    Statisztikákat készít és javaslatokat tesz, hogy hol tudunk spórolni.
    --
    - query : AbstractQuery
    --
    + getMounthlyStatistic() : AbstractQuery
    + getYearlyStatistic() : AbstractQuery
    + getStatisticFrom(String) : AbstractQuery
    + giveAdvice()
}

@enduml