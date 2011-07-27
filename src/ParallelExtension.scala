package org.nlogo.extensions.parallel

import org.nlogo.{ agent, api, nvm }
import api.Syntax.{ reporterSyntax, ReporterTaskType, ListType }
import api.ScalaConversions._
import scala.collection.parallel.CompositeThrowable

class ParallelExtension extends api.DefaultClassManager {
  def load(manager: api.PrimitiveManager) {
    manager.addPrimitive("map", new ParallelMap)
  }
}

class ParallelMap extends api.DefaultReporter {

  override def getSyntax =
    reporterSyntax(Array(ReporterTaskType, ListType),
                   ListType)

  // There's some ugly typecasting here because the api package doesn't yet have everything we need,
  // so we must cast to the implementation classes in the nvm package.
  override def report(args: Array[api.Argument], context: api.Context): api.LogoList = {
    val nContext = context.asInstanceOf[nvm.ExtensionContext].nvmContext
    val task = args(0).getReporterTask.asInstanceOf[nvm.ReporterLambda]
    val input = args(1).getList
    // def, not val, so we get a fresh context for each list item. we can't reuse the same context
    // when we're operating in parallel
    def freshContext = new nvm.Context(nContext, nContext.agent)
    try api.LogoList.fromVector(
      input.toVector.par.map(x =>
        task.report(freshContext, Array(x))).seq)
    catch handler
  }

  // It's not really clear what approach to error handling we should take, since the parallel
  // operations may have thrown multiple exceptions.  I guess dump all the stack traces to the
  // console and then in the GUI, report just the first error.
  val handler: PartialFunction[Throwable, Nothing] = {
    case ct: CompositeThrowable =>
      for(t <- ct.throwables)
        t.printStackTrace
      throw ct.throwables.head match {
        case e: Exception => new api.ExtensionException(e)
        case _ => ct
      }
  }

}
