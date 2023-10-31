package com.example.tackle.voteResult;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.votingBoard.entity.VotingBoard;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteResultServiceImpl implements VoteResultService {
    private final MemberRepository memberRepository;
    private final VoteResultRepository voteResultRepository;
    private final VoteItemsRepository voteItemsRepository;
    private final VotingBoardRepository votingBoardRepository;


    @Override
    public boolean register(Long itemId) {
        VoteItems voteItems = voteItemsRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

//        VoteResult voteResult = VoteResult.builder()
//                .createdAt(LocalDateTime.now())
//                .bettingPoint()
//                .build();


        return false;
    }

    @Override
    public boolean list2(Long resultId, String email) {
        return false;
    }

    @Override
    public List<VoteResult> list(String email) {
        String memberIdx = memberRepository.findByEmail(email).get().getIdx();

        List<VoteResult> voteResultList = voteResultRepository.findAllByIdx(memberIdx);

        for (VoteResult voteResult : voteResultList){

            Long postId = voteResult.getPostId();
            VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

                    boolean result = updateVotingStatusIfNeeded(votingBoard);
        // 게시글의 투표항목에서 투표수를 비교해 높은 투표항목를 가져옴
        // 높은 투표항목과 내 투표항목을 비교해 결과 출력
        if (result == false){


            List<VoteItems> voteItems3 = voteItemsRepository.findByPostIdOrderByVoteCountDesc(postId);

            System.out.println(voteItems3.size());
            for (VoteItems x : voteItems3){
                System.out.println(x.getVoteCount());
                System.out.println(x.getContent());
            }

            if (!voteItems3.isEmpty()) {
                Long minCount = voteItems3.get(0).getVoteCount();
                List<VoteItems> minCountItems = new ArrayList<>();

                for (VoteItems item : voteItems3) {
                    if (item.getVoteCount() == minCount) {
                        minCountItems.add(item);
                    } else if (item.getVoteCount() < minCount) {
                        minCount = item.getVoteCount();
                        minCountItems.clear();
                        minCountItems.add(item);
                    }
                }

                // minCountItems 리스트에는 가장 적은 카운트를 가진 VoteItems 객체들이 저장됩니다.
                // 이 리스트는 동일한 카운트를 가진 항목들을 모두 포함합니다.

                if (minCountItems.size() == 1) {
                    // 동일한 카운트가 없는 경우 다른 메소드를 실행
                    VoteItems voteItems2 = minCountItems.get(0);
                    Long result1 = voteItems2.getItemId();
                    System.out.println("result1 = " + result1);
                    Long result2 = voteResult.getItemId();
                    System.out.println("result2 = " + result2);

                    if (result1.equals(result2)) {
                        voteResult.setStatus(VotingResultStatus.LOSE);
                        System.out.println("LOSE");
                    } else {
                        voteResult.setStatus(VotingResultStatus.WIN);
                        System.out.println("WIN");
                    }
                } else {
                    // 동일한 카운트가 있는 경우 다른 처리를 수행하거나 필요한 로직을 추가
                    // minCountItems에는 동일한 카운트를 가진 항목들이 포함됩니다.
                    // 다른 처리를 수행하는 로직을 여기에 추가하세요.
                }
            } else {
                throw new CustomException(CustomExceptionCode.NOT_FOUND,"선택지가 없습니다.");
            }


        }

        voteResultRepository.save(voteResult);


        }


        return voteResultList;
    }


//    @Override
//    public boolean list(Long resultId, String memberEmail) {
//
//        String memberIdx = memberRepository.findByEmail(memberEmail).get().getIdx();
//
//        List<VoteResult> voteResultList = voteResultRepository.findAllByIdx(memberIdx);
//
//        for (VoteResult x : voteResultList){
//            System.out.println("idx : " +x.getIdx());
//            System.out.println("itemId : " +x.getItemId());
//            System.out.println("postId : " +x.getPostId());
//            System.out.println("===============");
//        }
//
//        VoteResult voteResult = voteResultRepository.findByResultIdAndIdx(resultId, memberIdx)
//                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));
//
//        Long postId = voteResult.getPostId();
//
//        VotingBoard votingBoard = votingBoardRepository.findById(postId)
//                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));
//
//        boolean result = updateVotingStatusIfNeeded(votingBoard);
//
//
//        // 게시글의 투표항목에서 투표수를 비교해 높은 투표항목를 가져옴
//        // 높은 투표항목과 내 투표항목을 비교해 결과 출력
//        if (result == false){
//
//
//            List<VoteItems> voteItems3 = voteItemsRepository.findByPostIdOrderByVoteCountDesc(postId);
//
//            // 투표가 동률일 경우 무승부
//            if (voteItems3.size() >= 2){
//                System.out.println("draw");
//
//                voteResult.setStatus(VotingResultStatus.DRAW);
//                voteResultRepository.save(voteResult);
//
//                return true;
//
//            }
//
//            VoteItems voteItems2 = voteItems3.get(0);
//
//            Long result1 = voteItems2.getItemId();
//            Long result2 = voteResult.getItemId();
//
//            if (result1.equals(result2)){
//                voteResult.setStatus(VotingResultStatus.WIN);
//                System.out.println("win");
//            } else {
//                voteResult.setStatus(VotingResultStatus.LOSE);
//                System.out.println("lose");
//            }
//
//        }
//        voteResultRepository.save(voteResult);
//
//
//        return true;
//    }



    private boolean updateVotingStatusIfNeeded(VotingBoard votingBoard) {
        LocalDateTime endDate = votingBoard.getEndDate();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (endDate != null && endDate.isBefore(currentDateTime)) {
            votingBoard.setStatus(VotingStatus.END);
            votingBoardRepository.save(votingBoard);

            return false;
        }
        return true;
    }

}
