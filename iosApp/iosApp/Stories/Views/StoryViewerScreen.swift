import SwiftUI

struct StoryViewerScreen: View {
    @StateObject private var viewModel = StoryViewerViewModel()
    @Environment(\.presentationMode) var presentationMode

    let storyGroup: StoryGroup?
    let isOwnStory: Bool

    init(storyGroup: StoryGroup? = nil, isOwnStory: Bool = false) {
        self.storyGroup = storyGroup
        self.isOwnStory = isOwnStory
    }

    var body: some View {
        ZStack {
            Color.black.edgesIgnoringSafeArea(.all)

            if viewModel.isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
            } else if viewModel.stories.isEmpty {
                 Text("No stories available")
                    .foregroundColor(.white)
            } else {
                let currentStory = viewModel.stories[viewModel.currentIndex]

                // Story Content Display
                ZStack {
                    if currentStory.type == .image {
                        AsyncImage(url: currentStory.mediaURL) { image in
                            image
                                .resizable()
                                .scaledToFit()
                        } placeholder: {
                            Color.gray
                        }
                    } else {
                        // Implement Video Player logic similar to Creator if needed
                         Color.gray.overlay(Text("Video Unsupported Mock"))
                    }

                    if let text = currentStory.textOverlay {
                        VStack {
                            Spacer()
                            Text(text)
                                .font(.title)
                                .foregroundColor(.white)
                                .padding()
                                .background(Color.black.opacity(0.5))
                                .cornerRadius(10)
                                .padding(.bottom, 50)
                        }
                    }

                    // Tap Areas for Navigation
                    GeometryReader { geometry in
                        HStack(spacing: 0) {
                            Color.clear
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    viewModel.previousStory()
                                }
                                .frame(width: geometry.size.width * 0.3)

                            Color.clear
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    viewModel.nextStory()
                                }
                                .onLongPressGesture(minimumDuration: .infinity, pressing: { isPressing in
                                    if isPressing {
                                        viewModel.pause()
                                    } else {
                                        viewModel.resume()
                                    }
                                }, perform: {})
                                .frame(width: geometry.size.width * 0.7)
                        }
                    }
                }
                .edgesIgnoringSafeArea(.all)
                .gesture(
                    DragGesture()
                        .onEnded { value in
                            if value.translation.height > 50 {
                                presentationMode.wrappedValue.dismiss()
                            }
                        }
                )

                // Overlay Header
                VStack {
                    HStack(spacing: 4) {
                        ForEach(0..<viewModel.stories.count, id: \.self) { index in
                            GeometryReader { geo in
                                Rectangle()
                                    .fill(Color.white.opacity(0.3))
                                    .frame(height: 3)
                                    .cornerRadius(1.5)
                                    .overlay(
                                        Rectangle()
                                            .fill(Color.white)
                                            .frame(width: index < viewModel.currentIndex ? geo.size.width : (index == viewModel.currentIndex ? geo.size.width * CGFloat(viewModel.progress) : 0), height: 3)
                                            .cornerRadius(1.5),
                                        alignment: .leading
                                    )
                            }
                            .frame(height: 3)
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 50) // Adjust for safe area

                    HStack {
                        Spacer()
                        Button(action: {
                            presentationMode.wrappedValue.dismiss()
                        }) {
                            Image(systemName: "xmark")
                                .font(.title2)
                                .foregroundColor(.white)
                                .padding()
                        }
                    }
                    Spacer()

                    // Bottom Controls Overlay
                    HStack {
                        if currentStory.isOwnStory {
                            HStack {
                                Image(systemName: "eye.fill")
                                Text("\(currentStory.viewsCount)")
                            }
                            .foregroundColor(.white)
                            .padding()
                            .background(Color.black.opacity(0.4))
                            .cornerRadius(20)
                            .padding(.horizontal)
                            .padding(.bottom, 20)

                            Spacer()
                        } else {
                            TextField("Reply...", text: $viewModel.replyText)
                                .padding()
                                .background(Color.black.opacity(0.4))
                                .cornerRadius(20)
                                .foregroundColor(.white)
                                .padding(.horizontal)
                                .padding(.bottom, 20)
                        }

                        if currentStory.type == .video {
                            Button(action: {
                                viewModel.toggleMute()
                            }) {
                                Image(systemName: viewModel.isMuted ? "speaker.slash.fill" : "speaker.wave.2.fill")
                                    .foregroundColor(.white)
                                    .padding()
                            }
                        }
                    }
                }
            }
        }
        .onAppear {
            if let group = storyGroup {
                viewModel.configure(with: group, isOwnStory: isOwnStory)
            }
        }
    }
}
