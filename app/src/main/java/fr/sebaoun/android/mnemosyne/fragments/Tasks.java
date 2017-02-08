package fr.sebaoun.android.mnemosyne.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import fr.sebaoun.android.mnemosyne.R;
import fr.sebaoun.android.mnemosyne.Task;
import fr.sebaoun.android.mnemosyne.TaskAdapter;
import fr.sebaoun.android.mnemosyne.TaskEditActivity;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sebaou_d on 16/01/17.
 */

public class Tasks extends Fragment {
    private RecyclerView        recyclerView = null;
    private TaskAdapter         taskAdapter = null;
    private Realm               realm;
    private RealmResults<Task>  tasks;

    /**
     * Override of the Fragment onCreate method that fetches the list of Tasks
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
        tasks = realm.where(Task.class).findAll();
    }

    /**
     * Override of the Fragment onCreateView method that binds the FloatingActionButton
     * with a listener so that it launches the TaskEditActivity to create a new Task.
     * It also initialises the RecyclerView and binds it with the TaskAdapter
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.add_fab);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent taskEditIntent = new Intent(getActivity(), TaskEditActivity.class);
                taskEditIntent.putExtra("id", -1);
                startActivity(taskEditIntent);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.task_recycler_view);
        taskAdapter = new TaskAdapter(tasks, new TaskCardOnClickListener());

        recyclerView.setAdapter(taskAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_all) {
            tasks = realm.where(Task.class).findAll();
            taskAdapter.swapData(tasks);
            return true;
        } else if (item.getItemId() == R.id.show_done) {
            tasks = realm.where(Task.class).equalTo("state", true).findAll();
            taskAdapter.swapData(tasks);
            return true;
        } else if (item.getItemId() == R.id.show_not_done) {
            tasks = realm.where(Task.class).equalTo("state", false).findAll();
            taskAdapter.swapData(tasks);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * onClickListener that is used to open the TaskEditActivity when a card Task Card is clicked
     * it fetches the id of the Task, puts it in an Intent so that the TaskEditActivity can fetch
     * the Task in Realm. Then launches the TaskEditActivity
     */
    class TaskCardOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildLayoutPosition(v);
            Task item = tasks.get(itemPosition);

            Intent taskEditIntent = new Intent(getActivity(), TaskEditActivity.class);
            taskEditIntent.putExtra("id", item.getId());
            startActivity(taskEditIntent);
        }
    }

    /**
     * Override of the Fragment onResume method that fetches the updated list of Tasks and
     * calls the TaskAdapter to update the RecyclerView with the new data
     */
    @Override
    public void onResume() {
        super.onResume();
        tasks = realm.where(Task.class).findAll();
        taskAdapter.swapData(tasks);
    }
}
