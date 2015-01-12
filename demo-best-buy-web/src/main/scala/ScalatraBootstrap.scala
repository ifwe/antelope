import javax.servlet.ServletContext

import co.ifwe.antelope.bestbuy._
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context.mount(new BestBuyDemoServlet, "/*")
  }
}
