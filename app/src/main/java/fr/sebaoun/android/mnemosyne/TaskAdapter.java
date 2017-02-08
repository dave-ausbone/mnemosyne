package fr.sebaoun.android.mnemosyne;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sebaou_d on 17/01/17.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> dataset;
    private View.OnClickListener onClickListener = null;

    /**
     * Class constructor that is used to initialise the dataset for the recyclerView
     * and to map a clickListener with the onClickEvent of every Card
     * @param dataset List containing all the Tasks that are to be displayed
     * @param clickListener ClickListener that will be used to handle the click of a Task Card
     */
    public TaskAdapter(List<Task> dataset, View.OnClickListener clickListener) {
        this.dataset = dataset;
        this.onClickListener = clickListener;
    }

    /**
     * Returns the number of tasks
     * @return datasetSize
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Binds each new Card with the data of a Task
     * @param holder Object that contains the card that is to be modified
     * @param position position of the Card in the RecyclerView
     */
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = dataset.get(position);
        holder.name.setText(task.getName());
        holder.comment.setText(task.getComment());
        holder.status.setText(task.getState() ? "DONE" : "TODO");
        holder.dueDate.setText(task.getDisplayableEnd());
    }

    /**
     * Inflates the newlly created Card
     * @param parent RecyclerView that contains the new Card
     * @param viewType Type of the view
     * @return TaskViewHolder
     */
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.task_card, parent, false);
        view.setOnClickListener(onClickListener);
        return new TaskViewHolder(view);
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView  name;
        TextView  comment;
        TextView  status;
        TextView  dueDate;

        public TaskViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.card_name);
            comment = (TextView) itemView.findViewById(R.id.card_comment);
            status = (TextView) itemView.findViewById(R.id.is_done);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
        }
    }

    /**
     * Replaces the current dataset with a new one
     * @param dataset Dataset that is to be put in the RecyclerView
     */
    public void swapData(List<Task> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
