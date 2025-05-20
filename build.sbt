scalaVersion in Global := "2.13.17-bin-060f47b-SNAPSHOT"

val p1 = project

val p2 = project.dependsOn(p1)
