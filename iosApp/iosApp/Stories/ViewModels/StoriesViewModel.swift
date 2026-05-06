import Foundation
import shared

struct StoryGroup: Identifiable {
    let id: String // userId
    let user: shared.User
    let stories: [shared.Story]
    var hasUnseen: Bool
}

@MainActor
class StoriesViewModel: ObservableObject {
    @Published var stories: [StoryGroup] = []
    @Published var isLoading = false
    @Published var error: String? = nil
    @Published var myStory: StoryGroup? = nil

    private let getStoriesUseCase = KMPHelper.sharedHelper.getStoriesUseCase
    // Placeholder for when you need auth info
    // private let currentUserId = ...

    init() {
        loadStories()
    }

    func loadStories() {
        self.isLoading = true
        self.error = nil

        let currentUserId = "mock_current_user_id" // Ideally KMPHelper.sharedHelper.authRepository.getCurrentUserId()

        Task {
            do {
                let domainStories = try await getStoriesUseCase.invoke()

                // Group stories by userId
                var groupedDict: [String: [shared.Story]] = [:]
                for story in domainStories {
                    groupedDict[story.userId, default: []].append(story)
                }

                // Temporary sorting and mapping
                // In a real app we would get the User object for each user id
                // and determine if they are seen or unseen based on local/remote state

                var newGroups: [StoryGroup] = []
                for (userId, userStories) in groupedDict {
                    // Create dummy user for now until we have full integration
                    let dummyUser = shared.User(
                        id: userId,
                        uid: userId,
                        email: nil,
                        username: "User",
                        nickname: nil,
                        displayName: "User \(String(userId.prefix(4)))",
                        name: nil,
                        bio: nil,
                        avatar: nil,
                        avatarHistoryType: "local",
                        profileCoverImage: nil,
                        coverImageUrl: nil,
                        accountPremium: false,
                        userLevelXp: 0,
                        verify: false,
                        isVerified: false,
                        isPrivate: false,
                        accountType: "user",
                        gender: "hidden",
                        banned: false,
                        status: .offline,
                        joinDate: nil,
                        joinedDate: 0,
                        oneSignalPlayerId: nil,
                        lastSeen: nil,
                        chattingWith: nil,
                        createdAt: nil,
                        updatedAt: nil,
                        followersCount: 0,
                        followingCount: 0,
                        postsCount: 0,
                        postCount: 0,
                        followerCount: 0,
                        location: nil,
                        relationshipStatus: nil,
                        birthday: nil,
                        work: nil,
                        education: nil,
                        currentCity: nil,
                        hometown: nil,
                        website: nil,
                        pronouns: nil,
                        linkedAccounts: [],
                        privacySettings: [:]
                    )

                    let group = StoryGroup(
                        id: userId,
                        user: dummyUser,
                        stories: userStories.sorted { ($0.createdAt ?? "") < ($1.createdAt ?? "") },
                        hasUnseen: true // Default to true for now
                    )

                    if userId == currentUserId {
                        self.myStory = group
                    } else {
                        newGroups.append(group)
                    }
                }

                // Own story is stored separately in `myStory`.
                // Sort the rest by unseen first, then by latest.
                self.stories = newGroups.sorted {
                    if $0.hasUnseen != $1.hasUnseen {
                        return $0.hasUnseen && !$1.hasUnseen
                    }
                    return $0.id < $1.id // fallback sort
                }
                self.isLoading = false
            } catch {
                self.error = error.localizedDescription
                self.isLoading = false
            }
        }
    }

    func markStoryAsSeen(_ storyId: String) {
        // TODO: Implement actual mark as seen logic using a shared use case
        // KMPHelper.sharedHelper.markStoryAsSeenUseCase.invoke(...)
        print("Marking story as seen: \(storyId)")
    }

    func deleteStory(_ storyId: String) {
        // TODO: Implement actual delete logic using a shared use case
        print("Deleting story: \(storyId)")
    }
}
