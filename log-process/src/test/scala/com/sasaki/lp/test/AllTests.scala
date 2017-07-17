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
}