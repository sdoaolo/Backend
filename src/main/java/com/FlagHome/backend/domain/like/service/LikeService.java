package com.FlagHome.backend.domain.like.service;

import com.FlagHome.backend.domain.like.entity.Like;
import com.FlagHome.backend.domain.like.enums.LikeType;
import com.FlagHome.backend.domain.like.repository.LikeRepository;
import com.FlagHome.backend.domain.member.entity.Member;
import com.FlagHome.backend.domain.member.repository.MemberRepository;
import com.FlagHome.backend.domain.post.entity.Post;
import com.FlagHome.backend.domain.post.repository.PostRepository;
import com.FlagHome.backend.domain.reply.entity.Reply;
import com.FlagHome.backend.domain.reply.repository.ReplyRepository;
import com.FlagHome.backend.global.exception.CustomException;
import com.FlagHome.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;

    @Transactional
    public void like(Long memberId, Long targetId, LikeType likeType) {
        Member member = findMember(memberId);
        if(likeType == LikeType.POST)
            likeInner(findPost(targetId).getLikeList(), targetId, member, likeType);
        else if(likeType == LikeType.REPLY)
            likeInner(findReply(targetId).getLikeList(), targetId, member, likeType);
    }

    @Transactional
    public void unlike(Long memberId, Long targetId, LikeType likeType) {
        Member member = findMember(memberId);
        if(likeType == LikeType.POST)
            unlikeInner(findPost(targetId).getLikeList(), member);
        else if(likeType == LikeType.REPLY)
            unlikeInner(findReply(targetId).getLikeList(), member);
    }

    private void likeInner(List<Like> likeList, Long targetId, Member member, LikeType likeType) {
        for(Like eachLike : likeList) {
            if(eachLike.getMember() == member)
                throw new CustomException(ErrorCode.ALREADY_EXISTS_LIKE);
        }

        likeList.add(Like.builder()
                .member(member)
                .targetId(targetId)
                .targetType(likeType)
                .build());
    }

    private void unlikeInner(List<Like> likeList, Member member) {
        Like deleteTargetLike = null;
        for (Like like : likeList) {
            if (like.getMember() == member) {
                deleteTargetLike = like;
                break;
            }
        }

        if(deleteTargetLike != null) {
            likeList.remove(deleteTargetLike);
            likeRepository.delete(deleteTargetLike);
        }
    }

    private Post findPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null)
            throw new CustomException(ErrorCode.POST_NOT_FOUND);

        return post;
    }

    private Reply findReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId).orElse(null);
        if(reply == null)
            throw new CustomException(ErrorCode.REPLY_NOT_FOUND);

        return reply;
    }

    private Member findMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if(member == null)
            throw new CustomException(ErrorCode.USER_NOT_FOUND);

        return member;
    }
}
