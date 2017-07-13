package com.sasaki.lp.test

import org.junit.{Assert, Test}
import com.sasaki.lp.poso.Task
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.SessionFactory
import com.sasaki.lp.persistence.QueryHelper._
import com.sasaki.lp.persistence.LppSchema._
import org.squeryl.Session

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
}