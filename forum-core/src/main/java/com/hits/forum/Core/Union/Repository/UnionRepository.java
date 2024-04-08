//package com.hits.forum.Core.Union.Repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public interface UnionRepository extends JpaRepository<Object, UUID> {
//
//    @Query(value = " ", nativeQuery = true)
//    List<Object[]> searchAll(@Param("content") String content);
//}
