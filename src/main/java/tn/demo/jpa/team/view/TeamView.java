package tn.demo.jpa.team.view;


import java.util.List;
import java.util.UUID;

public record TeamView(
        UUID id,
        String name,
        List<MemberView> members,
        List<TaskView> tasks
) {

    public ActualTimeSpent getSumOfActualWorkDone(){
        return tasks.stream()
                .filter(TaskView::isCompleted)
                .map(TaskView::actualTimeSpent)
                .reduce(ActualTimeSpent.zero(), ActualTimeSpent::add);
    }

}