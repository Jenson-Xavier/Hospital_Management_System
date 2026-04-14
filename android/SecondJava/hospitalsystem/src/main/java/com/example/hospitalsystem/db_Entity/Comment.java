package com.example.hospitalsystem.db_Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "comments",
        foreignKeys = {
                @ForeignKey(entity = Patient.class,
                        parentColumns = "userId",
                        childColumns = "publisher_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Doctor.class,
                        parentColumns = "userId",
                        childColumns = "commented_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("publisher_id"), @Index("commented_id")})
public class Comment {

    public static final int WAIT = 1;
    public static final int PASSED  = 2;
    public static final int UNPASSED = 3;

    public static final String[] stateStr = {
            "不限", "未审核", "通过", "未通过"
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comment_id")
    private int commentId;

    @ColumnInfo(name = "publisher_id")
    private String publisherId;

    @ColumnInfo(name = "commented_id")
    private String commentedId;

    @ColumnInfo(name = "publish_time")
    @NonNull
    private Date publishTime;

    @ColumnInfo(name = "comment_text")
    @NonNull
    private String commentText;

    @ColumnInfo(name = "audit")
    private int audit;

    // 插入评论若干
    private static final String[] mCommentedIdArray = {
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017"
    };

    private static final String[] mContentArray = {
           "医生非常专业，耐心解答了我所有的问题。",
           "他的诊断非常准确，治疗效果也很好。",
           "医生很友善，让我在整个治疗过程中感到很安心。",
           "医生的建议对我非常有帮助，我的病情明显好转。",
           "他总是详细解释每一个步骤，让我对治疗充满信心。",
           "医生很有耐心，从不急于下结论。",
           "她的专业知识让我印象深刻，值得信赖。",
           "医生的态度非常好，让我觉得被尊重。",
           "他总是准时，不让病人等待太久。",
           "医生很细心，注意到了我忽略的小细节。",
           "她的治疗方案很有效，我很快就康复了。",
           "医生总是倾听我的担忧，并给予中肯的建议。",
            "他的办公室环境很舒适，让人放松。",
            "她总是用简单的语言解释复杂的医学术语。",
            "医生很有责任心，跟进我的康复情况。",
            "医生的预约系统很方便，节省了很多时间。",
            "她总是鼓励我，让我对康复充满信心。"
    };

    public static ArrayList<Comment> getDefaultList() {
        ArrayList<Comment> commentList = new ArrayList<Comment>();
        for (int i = 1; i <= mCommentedIdArray.length; ++i) {
            Comment info = new Comment();
            info.setCommentId(i);
            info.setPublisherId("12312312312");
            info.setCommentedId(mCommentedIdArray[i-1]);
            info.setPublishTime(new Date(System.currentTimeMillis()));
            info.setCommentText(mContentArray[i-1]);
            info.setAudit(WAIT);
            commentList.add(info);
        }
        return commentList;
    }

    public Comment() {
        commentText = "";
        publishTime = null;
    }

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    @NonNull
    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(@NonNull String publisherId) {
        this.publisherId = publisherId;
    }

    @NonNull
    public String getCommentedId() {
        return commentedId;
    }

    public void setCommentedId(@NonNull String commentedId) {
        this.commentedId = commentedId;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public int getAudit() {
        return audit;
    }

    public void setAudit(int audit) {
        this.audit = audit;
    }
}
