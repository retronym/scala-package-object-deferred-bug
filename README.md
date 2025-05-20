# Bug Repro

1. Apply this patch to Scala 2.13

```diff
diff --git a/src/compiler/scala/tools/nsc/typechecker/Analyzer.scala b/src/compiler/scala/tools/nsc/typechecker/Analyzer.scala
--- a/src/compiler/scala/tools/nsc/typechecker/Analyzer.scala	(revision e19b69fb359fa908d84fe41a636e4b45c22a8ba3)
+++ b/src/compiler/scala/tools/nsc/typechecker/Analyzer.scala	(revision 812457de67fc3890741757cd1a7868783f77b9d3)
@@ -13,7 +13,9 @@
 package scala.tools.nsc
 package typechecker
 
+import scala.collection.mutable
 import scala.collection.mutable.ArrayDeque
+import scala.reflect.internal.util.JavaClearable
 
 /** Defines the sub-components for the namer, packageobjects, and typer phases.
  */
@@ -55,7 +57,14 @@
   object packageObjects extends {
     val global: Analyzer.this.global.type = Analyzer.this.global
   } with SubComponent {
-    val deferredOpen = perRunCaches.newSet[Symbol]()
+    val deferredOpen: mutable.Set[Symbol] = {
+      import scala.jdk.CollectionConverters._
+      // Thi
+      val javaSet = new java.util.LinkedHashSet[Symbol]()
+      perRunCaches.recordCache(JavaClearable.forCollection(javaSet))
+      javaSet.asScala
+    }
     val phaseName = "packageobjects"
     val runsAfter = List[String]()
     val runsRightAfter= Some("namer")

```

Compile this project:

```
sbt:scala-package-object-deferred-bug> ;p2/clean;p2/compile
[success] Total time: 0 s, completed 20 May 2025, 11:16:46 am
[info] compiling 2 Scala sources to /Users/jz/code/scala-package-object-deferred-bug/p2/target/scala-2.13/classes ...
[error] ## Exception when compiling 2 sources to /Users/jz/code/scala-package-object-deferred-bug/p2/target/scala-2.13/classes
[error] java.util.ConcurrentModificationException
[error] java.base/java.util.LinkedHashMap$LinkedHashIterator.nextNode(LinkedHashMap.java:1024)
[error] java.base/java.util.LinkedHashMap$LinkedKeyIterator.next(LinkedHashMap.java:1047)
[error] scala.collection.convert.JavaCollectionWrappers$JIteratorWrapper.next(JavaCollectionWrappers.scala:47)
[error] scala.collection.IterableOnceOps.foreach(IterableOnce.scala:617)
[error] scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:615)
[error] scala.collection.AbstractIterable.foreach(Iterable.scala:936)
[error] scala.tools.nsc.typechecker.Analyzer$packageObjects$$anon$2.apply(Analyzer.scala:92)
[error] scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:485)
[error] scala.tools.nsc.Global$GlobalPhase.run(Global.scala:432)
[error] scala.tools.nsc.Global$Run.compileUnitsInternal(Global.scala:1559)
[error] scala.tools.nsc.Global$Run.compileUnits(Global.scala:1543)
[error] scala.tools.nsc.Global$Run.compileSources(Global.scala:1535)
[error] scala.tools.nsc.Global$Run.compileFiles(Global.scala:1648)
[error] scala.tools.xsbt.CachedCompiler0.run(CompilerBridge.scala:176)
[error] scala.tools.xsbt.CachedCompiler0.run(CompilerBridge.scala:139)
[error] scala.tools.xsbt.CompilerBridge.run(CompilerBridge.scala:43)
[error] sbt.internal.inc.AnalyzingCompiler.compile(AnalyzingCompiler.scala:91)
[error] sbt.internal.inc.MixedAnalyzingCompiler.$anonfun$compile$7(MixedAnalyzingCompiler.scala:196)
[error] scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.java:23)
[error] sbt.internal.inc.MixedAnalyzingCompiler.timed(MixedAnalyzingCompiler.scala:252)
[error] sbt.internal.inc.MixedAnalyzingCompiler.$anonfun$compile$4(MixedAnalyzingCompiler.scala:186)
[error] sbt.internal.inc.MixedAnalyzingCompiler.$anonfun$compile$4$adapted(MixedAnalyzingCompiler.scala:166)
[error] sbt.internal.inc.JarUtils$.withPreviousJar(JarUtils.scala:241)
[error] sbt.internal.inc.MixedAnalyzingCompiler.compileScala$1(MixedAnalyzingCompiler.scala:166)
[error] sbt.internal.inc.MixedAnalyzingCompiler.compile(MixedAnalyzingCompiler.scala:214)
[error] sbt.internal.inc.IncrementalCompilerImpl.$anonfun$compileInternal$1(IncrementalCompilerImpl.scala:542)
[error] sbt.internal.inc.IncrementalCompilerImpl.$anonfun$compileInternal$1$adapted(IncrementalCompilerImpl.scala:542)
[error] sbt.internal.inc.Incremental$.$anonfun$apply$3(Incremental.scala:182)
[error] sbt.internal.inc.Incremental$.$anonfun$apply$3$adapted(Incremental.scala:180)
[error] sbt.internal.inc.Incremental$$anon$2.run(Incremental.scala:458)
[error] sbt.internal.inc.IncrementalCommon$CycleState.next(IncrementalCommon.scala:117)
[error] sbt.internal.inc.IncrementalCommon$$anon$1.next(IncrementalCommon.scala:56)
[error] sbt.internal.inc.IncrementalCommon$$anon$1.next(IncrementalCommon.scala:52)
[error] sbt.internal.inc.IncrementalCommon.cycle(IncrementalCommon.scala:263)
[error] sbt.internal.inc.Incremental$.$anonfun$incrementalCompile$8(Incremental.scala:413)
[error] sbt.internal.inc.Incremental$.withClassfileManager(Incremental.scala:500)
[error] sbt.internal.inc.Incremental$.incrementalCompile(Incremental.scala:400)
[error] sbt.internal.inc.Incremental$.apply(Incremental.scala:208)
[error] sbt.internal.inc.IncrementalCompilerImpl.compileInternal(IncrementalCompilerImpl.scala:542)
[error] sbt.internal.inc.IncrementalCompilerImpl.$anonfun$compileIncrementally$1(IncrementalCompilerImpl.scala:496)
[error] sbt.internal.inc.IncrementalCompilerImpl.handleCompilationError(IncrementalCompilerImpl.scala:332)
[error] sbt.internal.inc.IncrementalCompilerImpl.compileIncrementally(IncrementalCompilerImpl.scala:433)
[error] sbt.internal.inc.IncrementalCompilerImpl.compile(IncrementalCompilerImpl.scala:137)
[error] sbt.Defaults$.compileIncrementalTaskImpl(Defaults.scala:2443)
[error] sbt.Defaults$.$anonfun$compileIncrementalTask$2(Defaults.scala:2393)
[error] sbt.internal.server.BspCompileTask$.$anonfun$compute$1(BspCompileTask.scala:41)
[error] sbt.internal.io.Retry$.apply(Retry.scala:47)
[error] sbt.internal.io.Retry$.apply(Retry.scala:29)
[error] sbt.internal.io.Retry$.apply(Retry.scala:24)
[error] sbt.internal.server.BspCompileTask$.compute(BspCompileTask.scala:41)
[error] sbt.Defaults$.$anonfun$compileIncrementalTask$1(Defaults.scala:2391)
[error] scala.Function1.$anonfun$compose$1(Function1.scala:49)
[error] sbt.internal.util.$tilde$greater.$anonfun$$u2219$1(TypeFunctions.scala:63)
[error] sbt.std.Transform$$anon$4.work(Transform.scala:69)
[error] sbt.Execute.$anonfun$submit$2(Execute.scala:283)
[error] sbt.internal.util.ErrorHandling$.wideConvert(ErrorHandling.scala:24)
[error] sbt.Execute.work(Execute.scala:292)
[error] sbt.Execute.$anonfun$submit$1(Execute.scala:283)
[error] sbt.ConcurrentRestrictions$$anon$4.$anonfun$submitValid$1(ConcurrentRestrictions.scala:265)
[error] sbt.CompletionService$$anon$2.call(CompletionService.scala:65)
[error] java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
[error] java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
[error] java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
[error] java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
[error] java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
[error] java.base/java.lang.Thread.run(Thread.java:1575)
```