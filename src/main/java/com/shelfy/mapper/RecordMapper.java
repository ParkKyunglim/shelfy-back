package com.shelfy.mapper;

import com.shelfy.dto.record.RecordDataDTO;
import com.shelfy.dto.record.RecordStateDTO;
import com.shelfy.dto.record.RecordDTO;
import com.shelfy.dto.record.RecordRespDTO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/*
     날짜 : 2025/02/04
     이름 : 박연화
     내용 : RecordMapper 생성
*/
@Mapper
public interface RecordMapper {

    // tb_r_state ---------------------------------------------------------------------------------
    // 250204 박연화 bookId, stateType이 동일한 기록이 존재하는지 조회
    @Select("SELECT COUNT(*) FROM tb_r_state WHERE r_state_book_id = #{bookId} AND r_state_type = #{stateType} ")
    int selectStateByBookIdAndStateType(RecordDTO dto);

    // 250206 박연화 userId, bookId 동일한 state 기록이 존재하는지 조회
    @Select("SELECT r_state_id, r_state_book_id, r_state_user_id, r_state_type, r_state_created_at, r_state_updated_at " +
            "FROM tb_r_state " +
            "WHERE r_state_book_id = #{bookId} AND r_state_user_id = #{userId}")
    RecordStateDTO selectStateByBookIdAndUserId(RecordDTO dto);

    // 250204 박연화 record state 테이블 insert
    @Insert("INSERT INTO tb_r_state (r_state_book_id, r_state_user_id ,r_state_type) " +
            "VALUES (#{bookId}, #{userId}, #{stateType}) ")
    @Options(useGeneratedKeys = true, keyProperty = "stateId")
    void insertRecordState(RecordDTO dto);

    // 250206 박연화 record state 테이블 동일한 state id 를 가진 데이터 update
    @Update("UPDATE tb_r_state " +
            "SET r_state_type = #{rStateType}, r_state_updated_at = now() " +
            "WHERE r_state_id = #{rStateId} AND r_state_user_id = #{rStateUserId} ")
    @Options(keyProperty = "stateId")
    void updateRecordStateType(RecordStateDTO dto);


    // 250211 박연화 타입별 데이터 페이징 처리를 위한 총 데이터 갯수 조회
    @Select("SELECT COUNT(*) FROM tb_r_state WHERE r_state_user_id = #{userId} AND r_state_type = #{type}")
    int countRecordsByUserIdAndType(@Param("userId") int userId, @Param("type") int type);


    // 250213 박연화 유저별 모든 독서기록 조회
    @Select("SELECT * " +
            "FROM tb_r_state s " +
            "LEFT JOIN tb_r_done a ON s.r_state_id = a.r_done_state_id " +
            "LEFT JOIN tb_r_doing b ON s.r_state_id = b.r_doing_state_id " +
            "LEFT JOIN tb_r_wish c ON s.r_state_id = c.r_wish_state_id " +
            "LEFT JOIN tb_r_stop d ON s.r_state_id = d.r_stop_state_id " +
            "LEFT JOIN tb_my_book m ON s.r_state_book_id = m.my_book_id " +
            "WHERE s.r_state_user_id = #{userId} ")
    @Results({
            @Result(column = "r_state_id", property = "stateId"),
            @Result(column = "r_state_type", property = "stateType"),
            @Result(column = "r_state_book_id", property = "bookId"),
            @Result(column = "r_state_user_id", property = "userId"),

            @Result(column = "r_done_id", property = "recordId"),
            @Result(column = "r_done_rating", property = "rating"),
            @Result(column = "r_done_start_date", property = "startDate"),
            @Result(column = "r_done_end_date", property = "endDate"),
            @Result(column = "r_done_comment", property = "comment"),

            @Result(column = "r_doing_id", property = "recordId"),
            @Result(column = "r_doing_start_date", property = "startDate"),
            @Result(column = "r_doing_progress", property = "progress"),

            @Result(column = "r_wish_id", property = "recordId"),
            @Result(column = "r_wish_rating", property = "rating"),
            @Result(column = "r_wish_start_date", property = "startDate"),
            @Result(column = "r_wish_comment", property = "comment"),

            @Result(column = "r_stop_id", property = "recordId"),
            @Result(column = "r_stop_rating", property = "rating"),
            @Result(column = "r_stop_end_date", property = "endDate"),
            @Result(column = "r_stop_comment", property = "comment"),
            @Result(column = "r_stop_progress", property = "progress"),

            @Result(column = "my_book_image", property = "bookImage"),
            @Result(column = "my_book_page", property = "bookPage"),
            @Result(column = "my_book_title", property = "bookTitle"),
            @Result(column = "my_book_author", property = "bookAuthor"),
            @Result(column = "my_book_publisher", property = "bookPublisher"),
            @Result(column = "my_book_desc", property = "bookDesc"),
            @Result(column = "my_book_isbn", property = "bookIsbn")
    })
    List<RecordRespDTO> selectStateByUserId(@Param("userId") int userId);


    // tb_r_done ---------------------------------------------------------------------------------
    // 250207 박연화 stateId 가 동일한 record 조회
    @Select("SELECT r_done_id, r_done_state_id, r_done_start_date, r_done_end_date, r_done_rating, r_done_comment, r_done_active " +
            "FROM tb_r_done " +
            "WHERE r_done_state_id = #{stateId} ")
    @Results({
            @Result(column = "r_done_id", property = "recordId"),
            @Result(column = "r_done_state_id", property = "stateId"),
            @Result(column = "r_done_start_date", property = "startDate"),
            @Result(column = "r_done_end_date", property = "endDate"),
            @Result(column = "r_done_rating", property = "rating"),
            @Result(column = "r_done_comment", property = "comment"),
            @Result(column = "r_done_active", property = "active")
    })
    RecordDataDTO selectDoneByStateId(RecordDTO dto);


    // 250204 박연화 Done 테이블 insert
    @Insert("INSERT INTO tb_r_done " +
            "(r_done_state_id, r_done_start_date, r_done_end_date, r_done_rating, r_done_comment, r_done_active ) " +
            "VALUES (#{stateId}, #{startDate}, #{endDate}, #{rating}, #{comment}, #{active} )")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insertDone(RecordDTO dto);

    //250211 박연화 state/done 조인 , userid 조건 - select all + 페이징
    @Select("SELECT * " +
            "FROM tb_r_state s " +
            "JOIN tb_r_done d ON s.r_state_id = d.r_done_state_id " +
            "LEFT JOIN tb_my_book m ON s.r_state_book_id = m.my_book_id " +
            "WHERE s.r_state_user_id = #{userId} " +
            "AND s.r_state_type = 1 " +
            "LIMIT #{size} OFFSET #{offset}")
    @Results({
            @Result(column = "r_state_id", property = "stateId"),
            @Result(column = "r_state_type", property = "stateType"),
            @Result(column = "r_state_book_id", property = "bookId"),
            @Result(column = "r_state_user_id", property = "userId"),
            @Result(column = "r_done_id", property = "recordId"),
            @Result(column = "r_done_rating", property = "rating"),
            @Result(column = "r_done_start_date", property = "startDate"),
            @Result(column = "r_done_end_date", property = "endDate"),
            @Result(column = "r_done_comment", property = "comment"),
            @Result(column = "my_book_image", property = "bookImage"),
            @Result(column = "my_book_title", property = "bookTitle"),
            @Result(column = "my_book_author", property = "bookAuthor"),
            @Result(column = "my_book_publisher", property = "bookPublisher")
    })
    List<RecordRespDTO> selectDoneRecordsByUserId(@Param("userId") int userId, @Param("size") int size, @Param("offset") int offset);

    // tb_r_doing ---------------------------------------------------------------------------------
    // 250207 박연화 stateId 가 동일한 record 조회
    @Select("SELECT r_doing_id, r_doing_state_id, r_doing_start_date, r_doing_progress, r_doing_active " +
            "FROM tb_r_doing " +
            "WHERE r_doing_state_id = #{stateId} ")
    @Results({
            @Result(column = "r_doing_id", property = "recordId"),
            @Result(column = "r_doing_state_id", property = "stateId"),
            @Result(column = "r_doing_start_date", property = "startDate"),
            @Result(column = "r_doing_progress", property = "progress"),
            @Result(column = "r_doing_active", property = "active")
    })
    RecordDataDTO selectDoingByStateId(RecordDTO dto);


    // 250204 박연화 Doing 테이블 insert
    @Insert("INSERT INTO tb_r_doing " +
            "(r_doing_state_id, r_doing_start_date, r_doing_progress, r_doing_active ) " +
            "VALUES (#{stateId}, #{startDate}, #{progress}, #{active} )")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insertDoing(RecordDTO dto);


    //250211 박연화 state/doing 조인 , userid 조건 - select all + 페이징
    @Select("SELECT * " +
            "FROM tb_r_state s " +
            "JOIN tb_r_doing d ON s.r_state_id = d.r_doing_state_id " +
            "LEFT JOIN tb_my_book m ON s.r_state_book_id = m.my_book_id " +
            "WHERE s.r_state_user_id = #{userId} " +
            "AND s.r_state_type = 2 " +
            "LIMIT #{size} OFFSET #{offset}")
    @Results({
            @Result(column = "r_state_id", property = "stateId"),
            @Result(column = "r_state_type", property = "stateType"),
            @Result(column = "r_state_book_id", property = "bookId"),
            @Result(column = "r_state_user_id", property = "userId"),
            @Result(column = "r_doing_id", property = "recordId"),
            @Result(column = "r_doing_start_date", property = "startDate"),
            @Result(column = "r_doing_progress", property = "progress"),
            @Result(column = "my_book_image", property = "bookImage"),
            @Result(column = "my_book_title", property = "bookTitle"),
            @Result(column = "my_book_author", property = "bookAuthor"),
            @Result(column = "my_book_publisher", property = "bookPublisher")
    })
    List<RecordRespDTO> selectDoingRecordsByUserId(@Param("userId") int userId, @Param("size") int size, @Param("offset") int offset);


    // tb_r_wish ---------------------------------------------------------------------------------
    // 250207 박연화 stateId 가 동일한 record 조회
    @Select("SELECT r_wish_id, r_wish_state_id, r_wish_rating, r_wish_comment, r_wish_active " +
            "FROM tb_r_wish " +
            "WHERE r_wish_state_id = #{stateId} ")
    @Results({
            @Result(column = "r_wish_id", property = "recordId"),
            @Result(column = "r_wish_state_id", property = "stateId"),
            @Result(column = "r_wish_rating", property = "rating"),
            @Result(column = "r_wish_comment", property = "comment"),
            @Result(column = "r_wish_active", property = "active")
    })
    RecordDataDTO selectWishByStateId(RecordDTO dto);


    // 250204 박연화 Wish 테이블 insert
    @Insert("INSERT INTO tb_r_wish (r_wish_state_id, r_wish_rating, r_wish_comment, r_wish_active, r_wish_start_date ) " +
            "VALUES (#{stateId}, #{rating}, #{comment}, #{active}, #{startDate} )")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insertWish(RecordDTO dto);


    //250211 박연화 state/wish 조인 , userid 조건 - select all + 페이징
    @Select("SELECT * " +
            "FROM tb_r_state s " +
            "JOIN tb_r_wish d ON s.r_state_id = d.r_wish_state_id " +
            "LEFT JOIN tb_my_book m ON s.r_state_book_id = m.my_book_id " +
            "WHERE s.r_state_user_id = #{userId} " +
            "AND s.r_state_type = 3 " +
            "LIMIT #{size} OFFSET #{offset}")
    @Results({
            @Result(column = "r_state_id", property = "stateId"),
            @Result(column = "r_state_type", property = "stateType"),
            @Result(column = "r_state_book_id", property = "bookId"),
            @Result(column = "r_state_user_id", property = "userId"),
            @Result(column = "r_wish_id", property = "recordId"),
            @Result(column = "r_wish_rating", property = "rating"),
            @Result(column = "r_wish_start_date", property = "startDate"),
            @Result(column = "r_wish_comment", property = "comment"),
            @Result(column = "my_book_image", property = "bookImage"),
            @Result(column = "my_book_title", property = "bookTitle"),
            @Result(column = "my_book_author", property = "bookAuthor"),
            @Result(column = "my_book_publisher", property = "bookPublisher")
    })
    List<RecordRespDTO> selectWishRecordsByUserId(@Param("userId") int userId, @Param("size") int size, @Param("offset") int offset);


    // tb_r_stop ---------------------------------------------------------------------------------
    // 250207 박연화 stateId 가 동일한 record 조회
    @Select("SELECT r_stop_id, r_stop_state_id, r_stop_start_date, r_stop_end_date, r_stop_progress, r_stop_rating, r_stop_comment, r_stop_active " +
            "FROM tb_r_stop " +
            "WHERE r_stop_state_id = #{stateId} ")
    @Results({
            @Result(column = "r_stop_id", property = "recordId"),
            @Result(column = "r_stop_state_id", property = "stateId"),
            @Result(column = "r_stop_start_date", property = "startDate"),
            @Result(column = "r_stop_end_date", property = "endDate"),
            @Result(column = "r_stop_progress", property = "progress"),
            @Result(column = "r_stop_rating", property = "rating"),
            @Result(column = "r_stop_comment", property = "comment"),
            @Result(column = "r_stop_active", property = "active")
    })
    RecordDataDTO selectStopByStateId(RecordDTO dto);


    // 250204 박연화 Stop 테이블 insert
    @Insert("INSERT INTO tb_r_stop (r_stop_state_id, r_stop_start_date, r_stop_end_date, r_stop_progress, r_stop_rating, r_stop_comment, r_stop_active ) " +
            "VALUES (#{stateId}, #{startDate}, #{endDate}, #{progress}, #{rating}, #{comment}, #{active} )")
    @Options(useGeneratedKeys = true, keyProperty = "recordId")
    int insertStop(RecordDTO dto);


    //250211 박연화 state/stop 조인 , userid 조건 - select all + 페이징
    @Select("SELECT * " +
            "FROM tb_r_state s " +
            "JOIN tb_r_stop d ON s.r_state_id = d.r_stop_state_id " +
            "LEFT JOIN tb_my_book m ON s.r_state_book_id = m.my_book_id " +
            "WHERE s.r_state_user_id = #{userId} " +
            "AND s.r_state_type = 4 " +
            "LIMIT #{size} OFFSET #{offset}")
    @Results({
            @Result(column = "r_state_id", property = "stateId"),
            @Result(column = "r_state_type", property = "stateType"),
            @Result(column = "r_state_book_id", property = "bookId"),
            @Result(column = "r_state_user_id", property = "userId"),
            @Result(column = "r_stop_id", property = "recordId"),
            @Result(column = "r_stop_rating", property = "rating"),
            @Result(column = "r_stop_end_date", property = "endDate"),
            @Result(column = "r_stop_comment", property = "comment"),
            @Result(column = "r_stop_progress", property = "progress"),
            @Result(column = "my_book_image", property = "bookImage"),
            @Result(column = "my_book_title", property = "bookTitle"),
            @Result(column = "my_book_author", property = "bookAuthor"),
            @Result(column = "my_book_publisher", property = "bookPublisher")
    })
    List<RecordRespDTO> selectStopRecordsByUserId(@Param("userId") int userId, @Param("size") int size, @Param("offset") int offset);


    // delete 
    @Delete("DELETE FROM tb_r_done WHERE r_done_state_id = #{stateId}")
    int deleteDone(int stateId);

    @Delete("DELETE FROM tb_r_doing WHERE r_doing_state_id = #{stateId}")
    int deleteDoing(int stateId);

    @Delete("DELETE FROM tb_r_wish WHERE r_wish_state_id = #{stateId}")
    int deleteWish(int stateId);

    @Delete("DELETE FROM tb_r_stop WHERE r_stop_state_id = #{stateId}")
    int deleteStop(int stateId);

    @Delete("DELETE FROM tb_r_state WHERE r_state_id = #{stateId}")
    int deleteState(int stateId);



    // --- done 업데이트 ---
    @Update("UPDATE tb_r_done SET r_done_comment = #{comment} WHERE r_done_id = #{recordId}")
    void updateDoneComment(@Param("recordId") int recordId, @Param("comment") String comment);

    @Update("UPDATE tb_r_done SET r_done_start_date = #{startDate}, r_done_end_date = #{endDate} WHERE r_done_id = #{recordId}")
    void updateDoneDate(@Param("recordId") int recordId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Update("UPDATE tb_r_done SET r_done_rating = #{rating} WHERE r_done_id = #{recordId}")
    void updateDoneRating(@Param("recordId") int recordId, @Param("rating") double rating);


    // --- doing 업데이트 ---
    @Update("UPDATE tb_r_doing SET r_doing_progress = #{progress} WHERE r_doing_id = #{recordId}")
    void updateDoingProgress(@Param("recordId") int recordId, @Param("progress") int progress);

    @Update("UPDATE tb_r_doing SET r_doing_start_date = #{startDate} WHERE r_doing_id = #{recordId}")
    void updateDoingStartDate(@Param("recordId") int recordId, @Param("startDate") LocalDate startDate);


    // --- wish 업데이트 ---
    @Update("UPDATE tb_r_wish SET r_wish_comment = #{comment} WHERE r_wish_id = #{recordId}")
    void updateWishComment(@Param("recordId") int recordId, @Param("comment") String comment);

    @Update("UPDATE tb_r_wish SET r_wish_start_date = #{startDate} WHERE r_wish_id = #{recordId}")
    void updateWishStartDate(@Param("recordId") int recordId, @Param("startDate") LocalDate startDate);

    @Update("UPDATE tb_r_wish SET r_wish_rating = #{rating} WHERE r_wish_id = #{recordId}")
    void updateWishRating(@Param("recordId") int recordId, @Param("rating") double rating);


    // --- stop 업데이트 ---
    @Update("UPDATE tb_r_stop SET r_stop_progress = #{progress} WHERE r_stop_id = #{recordId}")
    void updateStopProgress(@Param("recordId") int recordId, @Param("progress") int progress);

    @Update("UPDATE tb_r_stop SET r_stop_comment = #{comment} WHERE r_stop_id = #{recordId}")
    void updateStopComment(@Param("recordId") int recordId, @Param("comment") String comment);

    @Update("UPDATE tb_r_stop SET r_stop_start_date = #{startDate}, r_stop_end_date = #{endDate} WHERE r_stop_id = #{recordId}")
    void updateStopStartDate(@Param("recordId") int recordId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Update("UPDATE tb_r_stop SET r_stop_rating = #{rating} WHERE r_stop_id = #{recordId}")
    void updateStopRating(@Param("recordId") int recordId, @Param("rating") double rating);


}
