package com.learning.projectmanager.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learning.projemanag.R
import com.learning.projectmanager.activities.TaskListActivity
import com.learning.projectmanager.models.TaskModel
import com.learning.projectmanager.utils.Constants
import java.util.Collections

/*
    Recycler Adapter that is used to handle all events associated with the Task Lists
    Shown in the Board Details Activity Screen
 */
open class BoardTasksAdapter(
    private val context: Context,
    private val list: ArrayList<TaskModel>
): RecyclerView.Adapter<BoardTasksAdapter.ViewHolder>() {
    private var mPosDraggedFrom = -1
    private var mPosDraggedTo = -1

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val addTaskList: TextView = view.findViewById(R.id.add_task_list)
        val taskItemLayout: LinearLayout = view.findViewById(R.id.task_item_layout)

        val taskListTitle: TextView = view.findViewById(R.id.task_list_title)
        val addTaskListName: CardView = view.findViewById(R.id.add_task_list_name)
        val closeCreateName: ImageButton = view.findViewById(R.id.close_create_name)
        val createName: EditText = view.findViewById(R.id.create_name)
        val submitCreateName: ImageButton = view.findViewById(R.id.submit_create_name)

        val viewTitleLayout: LinearLayout = view.findViewById(R.id.view_title)
        val editTitleCard: CardView = view.findViewById(R.id.edit_title_card)
        val editListTitleBtn: ImageView = view.findViewById(R.id.edit_list_title)
        val deleteList: ImageView = view.findViewById(R.id.delete_list)
        val editTitle: EditText = view.findViewById(R.id.edit_title)
        val closeEditTitle: ImageButton = view.findViewById(R.id.close_edit_title)
        val submitEditTitle: ImageButton = view.findViewById(R.id.submit_edit_title)

        val addCardEdit: CardView = view.findViewById(R.id.add_card)
        val addCardBtn: TextView = view.findViewById(R.id.add_card_to_list)
        val closeAddCard: ImageButton = view.findViewById(R.id.close_add_card)
        val addCardName: EditText = view.findViewById(R.id.add_card_title)
        val submitAddCard: ImageButton = view.findViewById(R.id.submit_add_card)

        val cardRecycler: RecyclerView = view.findViewById(R.id.card_list_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp().toPx()), 0, (40.toDp().toPx()), 0)
        view.layoutParams = layoutParams
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        if(position == list.size - 1 || list.size - 1 == 0) {
            holder.addTaskList.visibility = View.VISIBLE
            Log.i(this.javaClass.simpleName, holder.addTaskList.visibility.toString())
            holder.taskItemLayout.visibility =  View.GONE
        } else {
            holder.addTaskList.visibility = View.GONE
            holder.taskItemLayout.visibility =  View.VISIBLE
        }

        /*
            Displays the create task dialog
         */
        holder.taskListTitle.text = model.title
        holder.addTaskList.setOnClickListener {
            Log.i(this.javaClass.simpleName, "Here")
            holder.addTaskList.visibility = View.GONE
            holder.addTaskListName.visibility =  View.VISIBLE
        }

        holder.closeCreateName.setOnClickListener {
            holder.addTaskList.visibility = View.VISIBLE
            holder.addTaskListName.visibility =  View.GONE
        }

        holder.submitCreateName.setOnClickListener {
            val listName = holder.createName.text.toString()
            if(listName.isNotEmpty()) {
                if(context is TaskListActivity) {
                    context.createTaskList(listName)
                }
            } else {
                holder.addTaskList.visibility = View.VISIBLE
                holder.addTaskListName.visibility =  View.GONE
                Toast.makeText(
                    context,
                    "You cannot create a list without a name",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        /*
            Displays the edit task list title dialog
         */
        holder.editListTitleBtn.setOnClickListener {
            holder.editTitle.setText(model.title)
            holder.viewTitleLayout.visibility = View.GONE
            holder.editTitleCard.visibility = View.VISIBLE
        }

        holder.closeEditTitle.setOnClickListener {
            holder.viewTitleLayout.visibility = View.VISIBLE
            holder.editTitleCard.visibility =  View.GONE
        }

        holder.submitEditTitle.setOnClickListener {
            val listName = holder.editTitle.text.toString()
            if(listName.isNotEmpty()) {
                if(context is TaskListActivity) {
                    context.updateTaskList(holder.adapterPosition, listName, model)
                }
            } else {
                holder.viewTitleLayout.visibility = View.VISIBLE
                holder.editTitleCard.visibility =  View.GONE
                Toast.makeText(
                    context,
                    "Please Enter a Name",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.deleteList.setOnClickListener{
            Constants.dialogForDelete(context, holder.adapterPosition, model.title)
        }

        /*
            Displays the add card dialog
         */
        holder.addCardBtn.setOnClickListener {
            holder.addCardBtn.visibility = View.GONE
            holder.addCardEdit.visibility = View.VISIBLE
        }

        holder.closeAddCard.setOnClickListener {
            holder.addCardBtn.visibility = View.VISIBLE
            holder.addCardEdit.visibility = View.GONE
        }

        holder.submitAddCard.setOnClickListener {
            val cardName = holder.addCardName.text.toString()
            if(cardName.isNotEmpty()) {
                if(context is TaskListActivity) {
                    if (model.cardList.size == 0) {
                        context.createCardList(holder.adapterPosition, cardName)
                    } else {
                        context.addCardToList(holder.adapterPosition, cardName)
                    }
                } else {
                    Log.i(this.javaClass.simpleName, "Failed To Add Card to Task")
                    Toast.makeText(
                        context,
                        "Failed To Add Card to Task",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.i(this.javaClass.simpleName, "No Name Entered")
                Toast.makeText(
                    context,
                    "Please Enter a name for the card",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*
            Handles the card details recycler, sends user to card details when tapped
         */
        holder.cardRecycler.layoutManager = LinearLayoutManager(context)
        holder.cardRecycler.setHasFixedSize(true)
        val adapter = TaskCardsAdapter(context, model.cardList)
        holder.cardRecycler.adapter = adapter

        adapter.setOnClickListener(
            object: TaskCardsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if(context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            }
        )

        /*
            Handles Drag and Drop implementation, allows user to organise cards
         */
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )
        holder.cardRecycler.addItemDecoration(dividerItemDecoration)

        val helper = ItemTouchHelper(
            object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPos = viewHolder.adapterPosition
                    val targetPos = target.adapterPosition

                    if(mPosDraggedFrom == -1) mPosDraggedFrom = draggedPos
                    mPosDraggedTo = targetPos
                    Log.i(context.javaClass.simpleName, "Here")
                    Collections.swap(list[holder.adapterPosition].cardList, draggedPos, targetPos)
                    adapter.notifyItemMoved(draggedPos, targetPos)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    if(mPosDraggedFrom != -1 && mPosDraggedTo != -1 && mPosDraggedFrom != mPosDraggedTo)
                        (context as TaskListActivity).updateCardPosInTaskList(
                            holder.adapterPosition,
                            list[holder.adapterPosition].cardList
                        )

                    mPosDraggedFrom = -1
                    mPosDraggedTo = -1
                }
            }
        )
        helper.attachToRecyclerView(holder.cardRecycler)
    }

    /*
        Helper functions for converting PX to DP and vice versa
     */
    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this*Resources.getSystem().displayMetrics.density).toInt()

}