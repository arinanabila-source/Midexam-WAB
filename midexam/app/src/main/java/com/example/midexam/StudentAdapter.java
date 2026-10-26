package com.example.midexam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.LinearLayout;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    List<StudentModel> studentList;
    Context context;
    DBHelper dbHelper;

    public StudentAdapter(List<StudentModel> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item_list, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);
        holder.tvIdName.setText("ID: " + student.getStudentId() + " - " + student.getName());
        holder.tvMajor.setText("Major: " + student.getMajor());

        holder.btnDelete.setOnClickListener(v -> {
            dbHelper.deleteStudent(student.getId());
            studentList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, studentList.size());
            Toast.makeText(context, "Student Deleted", Toast.LENGTH_SHORT).show();
        });

        holder.btnEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Edit Student");

            View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_main, null);


            final EditText inputName = new EditText(context);
            inputName.setHint("Name");
            inputName.setText(student.getName());

            final EditText inputStudentId = new EditText(context);
            inputStudentId.setHint("Student ID");
            inputStudentId.setText(student.getStudentId());

            LinearLayout container = new LinearLayout(context);
            container.setOrientation(LinearLayout.VERTICAL);
            container.setPadding(32, 32, 32, 32);
            container.addView(inputName);
            container.addView(inputStudentId);
            builder.setView(container);

            builder.setPositiveButton("Update", (dialog, which) -> {
                String newName = inputName.getText().toString().trim();
                String newStudentId = inputStudentId.getText().toString().trim();

                if (!newName.isEmpty() && !newStudentId.isEmpty()) {
                    student.setName(newName);
                    student.setStudentId(newStudentId);
                    dbHelper.updateStudent(student);

                    studentList.set(position, student);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Student Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdName, tvMajor;
        Button btnEdit, btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdName = itemView.findViewById(R.id.tvIdName);
            tvMajor = itemView.findViewById(R.id.tvMajor);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
