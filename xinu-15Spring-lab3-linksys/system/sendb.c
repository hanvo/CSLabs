/* sendb.c - send */

#include <xinu.h>

syscall	sendb(
	  pid32		pid,		/* ID of recipient process	*/
	  umsg32	msg			/* contents of message		*/
	)
{
	intmask	mask;				/* saved interrupt mask		*/
	struct	procent *prptr;		/* ptr to process' table entry	*/
	struct	procent *myself;	/* ptr to process' table entry	*/


	mask = disable();
	if (isbadpid(pid)) {
		restore(mask);
		return SYSERR;
	}

	prptr = &proctab[pid];
	if (prptr->prstate == PR_FREE) {
		restore(mask);
		return SYSERR;
	}

	if(prptr->prhasmsg)
	{
		/*
		* 	Case 1: No one is waiting for reciever. -> passes through just like send() would
		* 	Case 2: Reciever is currently reading a different message 
		*		- Copy the sender's prmsg to sndmsg 
		*		- Change cndFlag = TRUE
		*		- Change process to PR_SND
		*		- resched()
		*/
		kprintf("ALREADY HAS MSG: Sedning Msg: %d\r\n", msg);
		myself = &proctab[currpid];
		myself->sndmsg = msg;
		myself->sndflag = TRUE;
		myself->prstate = PR_SND;
		enqueue(currpid,prptr->receivelist);
		resched();
		if(!prptr->prhasmsg)
		{
			prptr->prmsg = msg;			/* deliver message		*/
			prptr->prhasmsg = TRUE;		/* indicate message is waiting	*/
		}
	}
	else
	{
		kprintf("STANDARD: Sedning Msg: %d\r\n", msg);
		/*Case 1 */
		prptr->prmsg = msg;		/* deliver message		*/
		prptr->prhasmsg = TRUE;		/* indicate message is waiting	*/
		/* If recipient waiting or in timed-wait make it ready */
		if (prptr->prstate == PR_RECV) {
			ready(pid, RESCHED_YES);
		} else if (prptr->prstate == PR_RECTIM) {
			unsleep(pid);
			ready(pid, RESCHED_YES);
		}
	}



	restore(mask);		/* restore interrupts */
	return OK;
}
