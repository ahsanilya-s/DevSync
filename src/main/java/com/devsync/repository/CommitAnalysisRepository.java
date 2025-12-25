package com.devsync.repository;

import com.devsync.model.CommitAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommitAnalysisRepository extends JpaRepository<CommitAnalysis, Long> {
    List<CommitAnalysis> findByUserIdAndRepoOwnerAndRepoNameOrderByCommitDateDesc(String userId, String repoOwner, String repoName);
    CommitAnalysis findByCommitSha(String commitSha);
}
