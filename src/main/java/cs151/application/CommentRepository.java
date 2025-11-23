package cs151.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    // Example: use full name as a key. Swap to whatever you prefer later.
    public static List<Comment> loadForStudentName(String fullName) {
        List<Comment> list = new ArrayList<>();

        if ("Ari Nguyen".equalsIgnoreCase(fullName)) {
            list.add(new Comment(LocalDate.of(2025, 9, 12),
                    "Met about capstone. Needs to reduce dataset by ~30% to fit GPU."));
            list.add(new Comment(LocalDate.of(2025, 10, 3),
                    "Good progress. Accuracy +4.2%. Try cosine LR decay & AMP."));
        } else if ("Bella Santos".equalsIgnoreCase(fullName)) {
            list.add(new Comment(LocalDate.of(2025, 8, 21),
                    "Reviewed personal statement; suggested reordering for flow."));
        } else {
            list.add(new Comment(LocalDate.of(2025, 11, 18),
                    "General advisor note; keep weekly updates coming."));
        }

        return list;
    }
}
