package com.sasaki.lp.test

import org.junit.Assert
import org.junit.Test
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory

import com.sasaki.lp.persistence.LppSchema._
import com.sasaki.lp.persistence.QueryHelper._
import com.sasaki.lp.poso.Task

@Test
class AllTests extends Assert {
  @Test
  def main() {
   Assert.assertTrue(1 == 2)
  }
  
  @Test
  def testDb: Unit = {
    val task = new Task
//    task.taskId_=(1)
    task.taskName_=("session")
    task.createTime_=("2017-07-13")
    task.startTime_=("2017-07-13")
    task.finishTime_=("2017-07-13")
    task.taskType_=("1")
    task.taskStatus_=("1")
    task.taskParam_=("{}")
   
    val sessionFactory: SessionFactory = new SessionFactory { def newSession: Session = getSession().get() }
    
//    inTransaction(sessionFactory){
//       try {
//         $task.insert(task)
//       } catch {
//         case t: Throwable => t.printStackTrace() 
//       }
//    }
    
    transaction(sessionFactory)(println(queryById(1, $task).createTime))
  }
  
  @Test
  def testJSON = {
    import org.json4s._
    import org.json4s.jackson.JsonMethods._
    val json = """
    {"id": 1, "name": "sasaki", "seq": [1, 2, 3, 4]}
    """
    val jsonObj = parse(json, true)
    case class P(`id`: Int, `name`: String)    
    
   // println((jsonObj \ "id").values + " " + (jsonObj \ "name").values+ " " + (jsonObj \ "seq").values)
    implicit val formats = DefaultFormats 
  //    val p: P = jsonObj.extract[P]
    println(jsonObj.\("name").extract[String])
  }
  
  import com.sasaki.lp.enums.E._
  def key(k: String, s: String): String = 
    s.split(|).map(__ => (__.split(->)(0), __.split(->)(1))).takeWhile(_._1.equals(k))(0)._2
    
 
  @Test
  def testUtil {
    val str = "k1->v1|k2->v2|k3->v3"
    println(str.split(|).map(__ => (__.split(->)(0), __.split(->)(1))).takeWhile(__ => __._1 =="k2").size)
//    println(key("k2", str))      
  }
}