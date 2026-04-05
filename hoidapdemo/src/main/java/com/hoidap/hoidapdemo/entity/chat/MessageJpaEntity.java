package com.hoidap.hoidapdemo.entity.chat;

import com.hoidap.hoidapdemo.entity.enums.SenderType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
public class MessageJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationJpaEntity conversation;

    @Enumerated(EnumType.STRING)
    @Column(name = "nguoi_gui_type", nullable = false, length = 20)
    private SenderType nguoiGuiType;

    // Dùng String thay vì khoá ngoại cứng để hỗ trợ cả 2 đối tượng CVHT/SV. Nếu gán bằng BOT thì ID này có thể là null.
    @Column(name = "nguoi_gui_id", length = 10)
    private String nguoiGuiId;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String noiDung;

    @Column(name = "thoi_gian_gui", nullable = false)
    private LocalDateTime thoiGianGui;

    // Xử lý File như hệ thống cũ của bạn
    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_type", length = 255)
    private String fileType;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;
}
