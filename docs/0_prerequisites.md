# 💼 Prerequisites

You'll need a few things set up on your PC before starting:

- **Java Runtime Environment (JRE)** — the CLI tool runs on Java, so this is a must
- **`wget`** — for downloading files from the terminal. Windows users can skip this, there's a browser method explained below
- **`grep`** — optional, but makes searching through command output much easier. Windows users don't need it either
- Some basic comfort with using a terminal or command prompt helps, but you don't need to be an expert

---

## Step 0: Install Java

You need Java installed before anything else will work. Here's how to get it depending on your OS.

### Windows

Download and install **Eclipse Temurin** from [adoptium.net](https://adoptium.net/). This is the industry-standard build of OpenJDK and what most developers use. During installation, make sure to check the option that says **"Add to PATH"** so your terminal can find it.

Once installed, open PowerShell and run:

```powershell
java -version
```

You should see something like `openjdk version "21.x.x"`. If you do, you're good to go.

### Ubuntu / Debian / Mint / Pop!\_OS / Elementary / Zorin

```bash
sudo apt install openjdk-21-jre
```

### Fedora

```bash
sudo dnf install java-21-openjdk
```

### Arch / CachyOS / EndeavourOS / Manjaro

```bash
sudo pacman -S jre-openjdk
```

### Check it's installed (Linux / macOS)

```bash
java -version
```

Same as Windows, you should see an OpenJDK version printed out. Most Linux distros actually come with Java pre-installed, so there's a good chance this already works before you install anything.

## ⏭️ Whats next

The following section will show you how to use ReVanced CLI.

Continue: [🛠️ Using ReVanced CLI](1_usage.md)

New to this. Check out the beginner friendly guide instead.

Continue: [🛠️ Using ReVanced CLI (Beginner Guide)](1b_usage_beginners.md)
