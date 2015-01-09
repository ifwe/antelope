package co.ifwe.antelope.bestbuy

import org.scalatra._
import scalate.ScalateSupport

class BestBuyDemoServlet extends AntelopeBestBuyDemoStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
