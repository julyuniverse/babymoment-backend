rootProject.name = "babymoment"
include(":babymoment-api")
project(":babymoment-api").projectDir = file("api")
include(":client-aws")
project(":client-aws").projectDir = file("client/aws")