/*
 * @(#) 1.1 autoFVT/src/com/ibm/ws/wsfvt/build/tasks/ThreadPool.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/23/07 16:49:45 [8/8/12 06:40:28]
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 2004, 2007
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Change History:
 * Date        UserId      Feature/Defect  Description
 * -----------------------------------------------------------------------------
 * 05/23/2007  jramos      440922          New File
 */

/*
 * Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.ws.wsfvt.build.tasks;

/****************************************************************************
 * Place class description here.
 *
 * @author <a href='mailto:mattinger@yahoo.com'>Matthew Inger</a>
 *
 ****************************************************************************/


public class ThreadPool
{
    private int maxActive;
    private int active;


    public ThreadPool(int maxActive)
    {
        super();
        this.maxActive = maxActive;
        this.active = 0;
    }

    public void returnThread(ThreadPoolThread thread)
    {
        synchronized (this)
        {
            active--;
            notify();
        }
    }


    public ThreadPoolThread borrowThread()
        throws InterruptedException
    {
        synchronized (this)
        {
            if (maxActive > 0 && active >= maxActive)
            {
                wait();
            }

            active++;
            return new ThreadPoolThread(this);
        }
    }
}
