package controllers

import play.api._

import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.util.Random
import scala.concurrent.Future
import java.util.Date
import play.api.libs.json.JsValue
import play.api.libs.concurrent.Execution.Implicits.defaultContext


object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def get(id : String) = Action{
      Async {
        WS.url("http://localhost:9200/dpld/object/"+id).get.map{ response =>
          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
        }
      }
  }
  
  def update(id:String) = Action(parse.json) { implicit request =>
    Async {
        WS.url("http://localhost:9200/dpld/object/"+ id + "/_update").post(request.body).map{ response =>
          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
        }
      }
  }
  
  def put(id : String) = Action(parse.json){ implicit request =>
  Async {
        WS.url("http://localhost:9200/dpld/object/"+id).put(request.body).map{ response =>
          Ok("" + response.body).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
        }
      }
  }
  
  def delete(id : String) = Action { 
  Async {
        WS.url("http://localhost:9200/dpld/object/"+id).delete.map{ response =>
          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
        }
      }
  }
  
  def test() = Action {
    Async {
      WS.url("http://oki.orangeadd.com/vodka/1.0/categories").get.map{ response =>
          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
    }
    }
    
  }
  
//  def count(color : String) = Action{
//      Async {
//        WS.url("http://localhost:9200/dpld/object/_search?q=color:+"+color).get.map{ response =>
//          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
//        }
//      }
//  }
//  
  
 
  
  def populate = Action {
    
    for (i <- 1 to 1000000) yield {call(i)}
    
    Ok("hello");
//    val ListTest = new Array[Future[play.api.libs.ws.Response]](1000); 
//    var tab : String = ""
//    
//    for(i <- 1 to 1000) {
//      
//      var color = Array("blue", "red", "yellow")
//	  var shufflecolor  = Random.shuffle(color.toList).head  
//	  
//	  var volume = Array("cube", "sphere", "cylindre")
//	  var shufflevolume = Random.shuffle(volume.toList).head 
//	  
//	  var json : String = "{ \"id\"=" + i + ", \"name\"=\"name\", \"color\"=\""+shufflecolor+"\",\"volume\"=\""+shufflevolume+"\"}"
//	  
//	  val jsonFuture =  WS.url("http://localhost:9200/dpld/object/"+i).put(json).map { response => 
//	  
//	    Async(response)
//	    
//      }
//	    
//	  //ListTest.update(i, jsonFuture)
//	  Async(jsonFuture)
//	  
//    }
//    
//      for { 
//	    tab <- ListTest
//	  } yield {
//	    Ok("retour de")
//	  }
    
  }
  
  def call(i:Int) = {
    	var color = Array("blue", "red", "yellow")
	  var shufflecolor  = Random.shuffle(color.toList).head  
	  
	  var volume = Array("cube", "sphere", "cylindre")
	  var shufflevolume = Random.shuffle(volume.toList).head 
	  
	  var json : String = "{ \"id\":" + i + ", \"name\":\"name"+i+"\", \"color\":\""+shufflecolor+"\",\"volume\":\""+shufflevolume+"\"}"
	   
	  val jsonFuture =  WS.url("http://localhost:9200/dpld/object/"+i).put(json)
	  
	  println(new Date().getTime() + "  "+ i);

  }
  
  def update(indice:String, tocolor : String) = {
    
    Logger.debug("indice de couleur ="+indice+ " update avec couleur :+"+tocolor)
    
    val json : String = """{ "doc":{ "color":"""" + tocolor + """"}}"""
    
    Logger.debug(json)
    
    val jsonFuture =  WS.url("http://localhost:9200/dpld/object/"+indice+"/_update").post(json)
    Logger.debug(new Date().getTime() + "  "+ indice);
  }
  
  
   //Compte les objets 
  //ex d'Objet json a passer a la requete pour compter les objets rouges
//  {
//  "field": {
//      "color": {
//          "query": "red",
//          "default_operator": "AND"
//      }
//  }
// }
  
  
   def count= Action (parse.json){ implicit request =>
      Async {
        WS.url("http://localhost:9200/dpld/object/_count").post(request.body).map{ response =>
          Ok("" + response.json).withHeaders(CONTENT_TYPE -> "application/json;charset=utf-8")
        }
      }
  }
   
//  poru remplacer tous les objets jaune en bleu  
//  pour l'update le json elastic search a envoyer est celui ci 
//  { "doc":{
//    "color":"blue"
//  }
//}
   
   def bulkupdate(fromcolor : String, tocolor:String) = Action { 
     
     Async {
        WS.url("http://localhost:9200/dpld/object/_search?q=color:"+fromcolor+"&size=1000000&fields=id").get().map{ response =>
          val idfromcolor :  Seq[JsValue] = (response.json \ "hits" \ "hits" \\ "id")
          
          Logger.debug("idFromcolor" + idfromcolor.toString)
          
          var indice : String = ""
            
          
          for (indice <- idfromcolor) yield {update(indice.toString, tocolor)}
          
          Ok(""+idfromcolor.toString)
        }
      }
   }
   
   
}