package com.learning.projemanag.adapters

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learning.projemanag.R
import com.learning.projemanag.activities.TaskListActivity
import com.learning.projemanag.models.TaskModel

open class TaskItemsAdapter(
    private val context: Context,
    private val list: ArrayList<TaskModel>
): RecyclerView.Adapter<TaskItemsAdapter.ViewHolder>() {

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
                    context.updateTaskList(position, listName, model)
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
            dialogForDeleteList(position, model.title)
        }

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
                        context.createCardList(position, cardName)
                    } else {
                        context.addCardToList(position, cardName)
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

        holder.cardRecycler.layoutManager = LinearLayoutManager(context)
        holder.cardRecycler.setHasFixedSize(true)
        holder.cardRecycler.adapter = CardItemsAdapter(context, model.cardList)
    }

    private fun Int.toDp(): Int =
        (this/Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this*Resources.getSystem().displayMetrics.density).toInt()

    private fun dialogForDeleteList(position: Int, title: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("Are you sure you want to delete $title.\nThis Cannot be Undone.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                if(context is TaskListActivity) {
                    context.deleteTaskList(position)
                }
            }.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}