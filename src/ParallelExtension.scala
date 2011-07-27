import org.nlogo.api._
import Syntax._
import ScalaConversions._

class ParallelExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager) {
    manager.addPrimitive("map", new ParallelMap)
  }
}

class ParallelMap extends DefaultReporter {
  override def getSyntax = {
    import Syntax._
    reporterSyntax(Array(CommandTaskType, ListType),
                   ListType)
  }
  override def report(args: Array[Argument], context: Context): LogoList = {
  }
}
