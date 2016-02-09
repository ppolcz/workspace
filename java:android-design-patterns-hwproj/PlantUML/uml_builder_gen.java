title Builder Pattern - on specific problem

class Director <<Director>> {
 - builder
 + Construct() -- algorithm
}
note left: Construction process\n(construction ALGORITHM)
Director o-r- Builder

!define BUILDER #E0E0FF

class Builder <<Builder>> BUILDER {
 - REPRESENTATION
 + {abstract} ConvertCharacter(char)
 + {abstract} GET_REPRESENTATION()
}
note right: Data representation, here we store\nthe constructed product\n(desired PRODUCT)

class ConcBuilderA extends Builder {
 + ConvertCharacter(char)
}

class ConcBuilderB extends Builder {
 + ConvertCharacter(char)
}

legend right
 - az a cel, hogy reprezentaciot es a megvalositast kulon valasszuk, ezaltal tobb reprezentaciot is es
 megvalosithatova tegyunk.
 - van egy adatstruktura, amit at szeretnek alakitani kulonbozo hasonlo egyeb adatstrukturara
 van egy Java kodom ezt szeretnem atalakitani UML modelle, de ez lehet PlantUML, lehet XML strukturaju...
 Ilyenkor jo lehet a builder.
endlegend